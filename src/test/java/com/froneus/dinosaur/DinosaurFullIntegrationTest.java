package com.froneus.dinosaur;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.froneus.dinosaur.dto.CreateDinosaurRequest;
import com.froneus.dinosaur.dto.DinosaurResponse;
import com.froneus.dinosaur.dto.UpdateDinosaurRequest;
import com.froneus.dinosaur.model.Dinosaur;
import com.froneus.dinosaur.model.Status;
import com.froneus.dinosaur.repository.DinosaurRepository;
import com.froneus.dinosaur.service.DinosaurSchedulerService;
import com.froneus.dinosaur.service.RabbitMQSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DinosaurFullIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DinosaurRepository dinosaurRepository;

    @Autowired
    private DinosaurSchedulerService schedulerService;

    @MockitoBean
    private RabbitMQSenderService rabbitMQSenderService;

    @BeforeEach
    void setUp() {
        dinosaurRepository.deleteAll();
    }

    @Test
    @DisplayName("Full Workflow: Create, Update, Get, Delete and Validations")
    void fullWorkflowTest() throws Exception {
        // 1. POST /dinosaur - Create (Status must be ALIVE)
        CreateDinosaurRequest createRequest = new CreateDinosaurRequest();
        createRequest.setName("T-Rex");
        createRequest.setSpecies("Theropod");
        createRequest.setDiscoveryDate(LocalDateTime.now().minusDays(10));
        createRequest.setExtinctionDate(LocalDateTime.now().plusDays(10));

        String createResponseJson = mockMvc.perform(post("/dinosaur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ALIVE"))
                .andReturn().getResponse().getContentAsString();

        DinosaurResponse createdDinosaur = objectMapper.readValue(createResponseJson, DinosaurResponse.class);
        String dinosaurId = createdDinosaur.getId();

        // 2. Validation: Unique Name
        mockMvc.perform(post("/dinosaur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());

        // 3. Validation: discoveryDate < extinctionDate
        createRequest.setName("Invalid Date Dino");
        createRequest.setDiscoveryDate(LocalDateTime.now().plusDays(5));
        createRequest.setExtinctionDate(LocalDateTime.now());
        mockMvc.perform(post("/dinosaur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());

        // 4. PUT /dinosaur/{id} - Update
        UpdateDinosaurRequest updateRequest = new UpdateDinosaurRequest();
        updateRequest.setName("Updated T-Rex");
        updateRequest.setSpecies("Theropod Updated");
        updateRequest.setDiscoveryDate(LocalDateTime.now().minusDays(11));
        updateRequest.setExtinctionDate(LocalDateTime.now().plusDays(11));
        updateRequest.setStatus(Status.ENDANGERED);

        mockMvc.perform(put("/dinosaur/" + dinosaurId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated T-Rex"))
                .andExpect(jsonPath("$.status").value("ENDANGERED"));

        // 5. Validation: Cannot modify EXTINCT dinosaur
        Dinosaur dino = dinosaurRepository.findById(dinosaurId).get();
        dino.setStatus(Status.EXTINCT);
        dinosaurRepository.save(dino);

        mockMvc.perform(put("/dinosaur/" + dinosaurId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Scheduler: ALIVE to ENDANGERED (24h before)")
    void schedulerEndangeredTest() {
        // Create dino that will be endangered soon
        Dinosaur dino = new Dinosaur();
        dino.setName("Near Extinction Dino");
        dino.setSpecies("Species");
        dino.setStatus(Status.ALIVE);
        dino.setDiscoveryDate(LocalDateTime.now().minusDays(100));
        dino.setExtinctionDate(LocalDateTime.now().plusHours(23)); // Less than 24h
        dinosaurRepository.save(dino);

        // Run scheduler manually
        schedulerService.updateEndangeredStatus();

        Dinosaur updatedDino = dinosaurRepository.findById(dino.getId()).get();
        assertEquals(Status.ENDANGERED, updatedDino.getStatus());

        // Verify messaging
        await().atMost(Duration.ofSeconds(2)).untilAsserted(() ->
                verify(rabbitMQSenderService, atLeastOnce()).sendStatusMessage(eq(updatedDino.getId()), eq(Status.ENDANGERED))
        );
    }

    @Test
    @DisplayName("Scheduler: Any to EXTINCT (on extinctionDate)")
    void schedulerExtinctTest() {
        Dinosaur dino = new Dinosaur();
        dino.setName("Extinct Dino");
        dino.setSpecies("Species");
        dino.setStatus(Status.ENDANGERED);
        dino.setDiscoveryDate(LocalDateTime.now().minusDays(100));
        dino.setExtinctionDate(LocalDateTime.now().minusMinutes(1)); // Already past
        dinosaurRepository.save(dino);

        // Run scheduler manually
        schedulerService.updateExtinctStatus();

        Dinosaur updatedDino = dinosaurRepository.findById(dino.getId()).get();
        assertEquals(Status.EXTINCT, updatedDino.getStatus());

        // Verify messaging
        await().atMost(Duration.ofSeconds(2)).untilAsserted(() ->
                verify(rabbitMQSenderService, atLeastOnce()).sendStatusMessage(eq(updatedDino.getId()), eq(Status.EXTINCT))
        );
    }
}
