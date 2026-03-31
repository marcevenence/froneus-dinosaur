package com.froneus.dinosaur.controller;

import com.froneus.dinosaur.dto.CreateDinosaurRequest;
import com.froneus.dinosaur.dto.UpdateDinosaurRequest;
import com.froneus.dinosaur.model.Dinosaur;
import com.froneus.dinosaur.service.DinosaurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dinosaur")
@RequiredArgsConstructor
@Tag(name = "Dinosaur Management", description = "Endpoints for managing dinosaurs")
public class DinosaurController {

    private final DinosaurService dinosaurService;

    @GetMapping
    @Operation(summary = "Get all dinosaurs", description = "Returns a list of all dinosaurs in the database")
    public List<Dinosaur> getAllDinosaurs() {
        return dinosaurService.getAllDinosaurs();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get dinosaur by ID", description = "Returns details of a specific dinosaur")
    @ApiResponse(responseCode = "200", description = "Dinosaur found")
    @ApiResponse(responseCode = "404", description = "Dinosaur not found")
    public ResponseEntity<Dinosaur> getDinosaurById(@PathVariable String id) {
        return ResponseEntity.ok(dinosaurService.getDinosaurById(id));
    }

    @PostMapping
    @Operation(summary = "Create a dinosaur", description = "Creates a new dinosaur")
    @ApiResponse(responseCode = "201", description = "Dinosaur created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input or name already exists")
    public ResponseEntity<Dinosaur> createDinosaur(@Valid @RequestBody CreateDinosaurRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dinosaurService.createDinosaur(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a dinosaur", description = "Updates an existing dinosaur")
    @ApiResponse(responseCode = "200", description = "Dinosaur updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input or trying to update an EXTINCT dinosaur")
    @ApiResponse(responseCode = "404", description = "Dinosaur not found")
    public ResponseEntity<Dinosaur> updateDinosaur(@PathVariable String id, @Valid @RequestBody UpdateDinosaurRequest request) {
        return ResponseEntity.ok(dinosaurService.updateDinosaur(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a dinosaur", description = "Removes a dinosaur from the database")
    @ApiResponse(responseCode = "204", description = "Dinosaur deleted successfully")
    @ApiResponse(responseCode = "404", description = "Dinosaur not found")
    public ResponseEntity<Void> deleteDinosaur(@PathVariable String id) {
        dinosaurService.deleteDinosaur(id);
        return ResponseEntity.noContent().build();
    }
}
