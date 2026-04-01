package com.froneus.dinosaur.service;

import com.froneus.dinosaur.dto.CreateDinosaurRequest;
import com.froneus.dinosaur.dto.DinosaurResponse;
import com.froneus.dinosaur.dto.UpdateDinosaurRequest;
import com.froneus.dinosaur.exception.ResourceNotFoundException;
import com.froneus.dinosaur.mapper.DinosaurMapper;
import com.froneus.dinosaur.model.Dinosaur;
import com.froneus.dinosaur.model.Status;
import com.froneus.dinosaur.repository.DinosaurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DinosaurServiceTest {

    @Mock
    private DinosaurRepository dinosaurRepository;

    @Mock
    private DinosaurMapper dinosaurMapper;

    @Mock
    private RabbitMQSenderService rabbitMQSenderService;

    @InjectMocks
    private DinosaurService dinosaurService;

    private Dinosaur dinosaur;
    private CreateDinosaurRequest createRequest;

    @BeforeEach
    void setUp() {
        dinosaur = new Dinosaur();
        dinosaur.setId("1");
        dinosaur.setName("T-Rex");
        dinosaur.setStatus(Status.ALIVE);
        dinosaur.setDiscoveryDate(LocalDateTime.now().minusDays(10));
        dinosaur.setExtinctionDate(LocalDateTime.now().plusDays(10));

        createRequest = new CreateDinosaurRequest();
        createRequest.setName("T-Rex");
        createRequest.setDiscoveryDate(LocalDateTime.now().minusDays(10));
        createRequest.setExtinctionDate(LocalDateTime.now().plusDays(10));
    }

    @Test
    @DisplayName("Should create dinosaur successfully with ALIVE status")
    void createDinosaur_Success() {
        when(dinosaurMapper.toEntity(any())).thenReturn(dinosaur);
        when(dinosaurRepository.existsByName(anyString())).thenReturn(false);
        when(dinosaurRepository.save(any())).thenReturn(dinosaur);
        when(dinosaurMapper.toDTO(any())).thenReturn(new DinosaurResponse());

        DinosaurResponse response = dinosaurService.createDinosaur(createRequest);

        assertNotNull(response);
        assertEquals(Status.ALIVE, dinosaur.getStatus());
    }

    @Test
    @DisplayName("Should throw exception when discoveryDate is after extinctionDate")
    void createDinosaur_InvalidDates() {
        createRequest.setDiscoveryDate(LocalDateTime.now().plusDays(10));
        createRequest.setExtinctionDate(LocalDateTime.now());

        when(dinosaurMapper.toEntity(any())).thenReturn(dinosaur);
        dinosaur.setDiscoveryDate(createRequest.getDiscoveryDate());
        dinosaur.setExtinctionDate(createRequest.getExtinctionDate());

        assertThrows(IllegalArgumentException.class, () -> dinosaurService.createDinosaur(createRequest));
    }

    @Test
    @DisplayName("Should throw exception when trying to update an EXTINCT dinosaur")
    void updateDinosaur_ExtinctFail() {
        dinosaur.setStatus(Status.EXTINCT);
        DinosaurResponse responseDto = new DinosaurResponse();
        responseDto.setStatus(Status.EXTINCT);

        when(dinosaurRepository.findById(anyString())).thenReturn(Optional.of(dinosaur));
        when(dinosaurMapper.toDTO(any())).thenReturn(responseDto);

        UpdateDinosaurRequest updateRequest = new UpdateDinosaurRequest();

        assertThrows(IllegalArgumentException.class, () -> dinosaurService.updateDinosaur("1", updateRequest));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when dinosaur does not exist")
    void getDinosaurById_NotFound() {
        when(dinosaurRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> dinosaurService.getDinosaurById("999"));
    }
}
