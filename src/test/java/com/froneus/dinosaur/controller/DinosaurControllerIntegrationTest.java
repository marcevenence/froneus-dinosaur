package com.froneus.dinosaur.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.froneus.dinosaur.dto.CreateDinosaurRequest;
import com.froneus.dinosaur.dto.DinosaurResponse;
import com.froneus.dinosaur.model.Status;
import com.froneus.dinosaur.service.DinosaurService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DinosaurControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DinosaurService dinosaurService;

    @Test
    @DisplayName("POST /dinosaur - Should create dinosaur and return 201")
    void createDinosaur_ReturnsCreated() throws Exception {
        CreateDinosaurRequest request = new CreateDinosaurRequest();
        request.setName("Velociraptor");
        request.setSpecies("Dromaeosaurid");
        request.setDiscoveryDate(LocalDateTime.now().minusYears(100));
        request.setExtinctionDate(LocalDateTime.now().plusYears(100));

        DinosaurResponse response = new DinosaurResponse();
        response.setId("123");
        response.setName("Velociraptor");
        response.setStatus(Status.ALIVE);

        when(dinosaurService.createDinosaur(any(CreateDinosaurRequest.class))).thenReturn(response);

        mockMvc.perform(post("/dinosaur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Velociraptor"))
                .andExpect(jsonPath("$.status").value("ALIVE"));
    }

    @Test
    @DisplayName("GET /dinosaur - Should return list of dinosaurs")
    void getAllDinosaurs_ReturnsList() throws Exception {
        when(dinosaurService.getAllDinosaurs()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/dinosaur"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /dinosaur/{id} - Should return 404 when not found")
    void getDinosaurById_NotFound() throws Exception {
        when(dinosaurService.getDinosaurById("999")).thenThrow(new com.froneus.dinosaur.exception.ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/dinosaur/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("POST /dinosaur - Should return 400 when validation fails")
    void createDinosaur_ValidationFail() throws Exception {
        CreateDinosaurRequest invalidRequest = new CreateDinosaurRequest();
        // Empty name to trigger @NotBlank

        mockMvc.perform(post("/dinosaur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }
}
