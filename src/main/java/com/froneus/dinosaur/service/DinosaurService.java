package com.froneus.dinosaur.service;

import com.froneus.dinosaur.dto.CreateDinosaurRequest;
import com.froneus.dinosaur.dto.DinosaurResponse;
import com.froneus.dinosaur.dto.UpdateDinosaurRequest;
import com.froneus.dinosaur.exception.ResourceNotFoundException;
import com.froneus.dinosaur.mapper.DinosaurMapper;
import com.froneus.dinosaur.model.Dinosaur;
import com.froneus.dinosaur.model.Status;
import com.froneus.dinosaur.repository.DinosaurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DinosaurService {

    private final DinosaurRepository dinosaurRepository;
    private final DinosaurMapper dinosaurMapper;
    private final RabbitMQSenderService rabbitMQSenderService;

    public List<DinosaurResponse> getAllDinosaurs() {
        return dinosaurMapper.toDinosaurResponseList(dinosaurRepository.findAll());
    }

    public DinosaurResponse getDinosaurById(String id) {
        return dinosaurMapper.toDTO(dinosaurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dinosaur not found with id: " + id)));
    }

    public DinosaurResponse createDinosaur(CreateDinosaurRequest request) {
        Dinosaur dinosaur = dinosaurMapper.toEntity(request);

        validateDinosaurDates(dinosaur);
        validateUniqueName(dinosaur.getName());

        dinosaur.setStatus(Status.ALIVE); // Initial status must be ALIVE
        return dinosaurMapper.toDTO(dinosaurRepository.save(dinosaur));
    }

    public DinosaurResponse updateDinosaur(String id, UpdateDinosaurRequest request) {
        DinosaurResponse existingDinosaur = getDinosaurById(id);

        if (Status.EXTINCT.equals(existingDinosaur.getStatus())) {
            throw new IllegalArgumentException("It is not possible to modify an EXTINCT dinosaur.");
        }

        if (!existingDinosaur.getName().equals(request.getName())) {
            validateUniqueName(request.getName());
        }

        Dinosaur dinosaur = dinosaurMapper.toEntity(id, request);
        validateDinosaurDates(dinosaur);

        dinosaurRepository.save(dinosaur);

        // Send message if Status changes
        if (!existingDinosaur.getStatus().equals(request.getStatus())) {
            rabbitMQSenderService.sendStatusMessage(dinosaur.getId(), dinosaur.getStatus());
        }

        return dinosaurMapper.toDTO(dinosaur);
    }

    public void deleteDinosaur(String id) {
        if (!dinosaurRepository.existsById(id)) {
            throw new ResourceNotFoundException("Dinosaur not found with id: " + id);
        }
        dinosaurRepository.deleteById(id);
    }

    /**
     * Business rules for dates
     *
     * @param dinosaur The entity to validate
     */
    private void validateDinosaurDates(Dinosaur dinosaur) {
        if (dinosaur.getDiscoveryDate().isAfter(dinosaur.getExtinctionDate()) ||
                dinosaur.getDiscoveryDate().isEqual(dinosaur.getExtinctionDate())) {
            throw new IllegalArgumentException("The value of discoveryDate cannot be greater than or equal to the value of extinctionDate.");
        }
    }

    /**
     * Business rule for unique name
     *
     * @param name The name to validate
     */
    private void validateUniqueName(String name) {
        if (dinosaurRepository.existsByName(name)) {
            throw new IllegalArgumentException("The dinosaur name must be unique.");
        }
    }
}
