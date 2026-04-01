package com.froneus.dinosaur.service;

import com.froneus.dinosaur.dto.CreateDinosaurRequest;
import com.froneus.dinosaur.dto.DinosaurResponse;
import com.froneus.dinosaur.model.Status;
import com.froneus.dinosaur.repository.DinosaurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class DinosaurServiceIntegrationTest {

    @Autowired
    private DinosaurService dinosaurService;

    @Autowired
    private DinosaurRepository dinosaurRepository;

    @MockitoBean
    private RabbitMQSenderService rabbitMQSenderService;

    @BeforeEach
    void setUp() {
        dinosaurRepository.deleteAll();
    }

    @Test
    @DisplayName("Should persist dinosaur in MongoDB")
    void createDinosaur_IntegrationSuccess() {
        // Arrange
        CreateDinosaurRequest request = new CreateDinosaurRequest();
        request.setName("Triceratops");
        request.setSpecies("Ceratopsid");
        request.setDiscoveryDate(LocalDateTime.now().minusDays(1));
        request.setExtinctionDate(LocalDateTime.now().plusDays(1));

        // Act
        DinosaurResponse response = dinosaurService.createDinosaur(request);

        // Assert
        assertNotNull(response.getId());
        assertTrue(dinosaurRepository.existsByName("Triceratops"));
        assertEquals(Status.ALIVE, response.getStatus());
    }

    @Test
    @DisplayName("Should fail when creating dinosaur with existing name")
    void createDinosaur_DuplicateNameFail() {
        // Arrange
        CreateDinosaurRequest request = new CreateDinosaurRequest();
        request.setName("Stegosaurus");
        request.setSpecies("Stegosaurid");
        request.setDiscoveryDate(LocalDateTime.now().minusDays(1));
        request.setExtinctionDate(LocalDateTime.now().plusDays(1));

        dinosaurService.createDinosaur(request);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> dinosaurService.createDinosaur(request));
    }
}
