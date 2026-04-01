package com.froneus.dinosaur.controller;

import com.froneus.dinosaur.dto.CreateDinosaurRequest;
import com.froneus.dinosaur.dto.DinosaurResponse;
import com.froneus.dinosaur.dto.ErrorResponse;
import com.froneus.dinosaur.dto.UpdateDinosaurRequest;
import com.froneus.dinosaur.service.DinosaurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @ApiResponse(responseCode = "200",
            description = "List of dinosaurs found",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = DinosaurResponse.class))
            ))
    public List<DinosaurResponse> getAllDinosaurs() {
        return dinosaurService.getAllDinosaurs();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get dinosaur by ID", description = "Returns details of a specific dinosaur")
    @ApiResponse(responseCode = "200",
            description = "Dinosaur found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DinosaurResponse.class)
            ))
    @ApiResponse(responseCode = "404",
            description = "Dinosaur not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    public ResponseEntity<DinosaurResponse> getDinosaurById(@Parameter(description = "Dinosaur ID", required = true, example = "69cc0a3b3ad0e659fad805db") @PathVariable String id) {
        return ResponseEntity.ok(dinosaurService.getDinosaurById(id));
    }

    @PostMapping
    @Operation(summary = "Create a dinosaur", description = "Creates a new dinosaur")
    @ApiResponse(responseCode = "201",
            description = "Dinosaur created successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DinosaurResponse.class)
            ))
    @ApiResponse(responseCode = "400",
            description = "Invalid input or name already exists",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    public ResponseEntity<DinosaurResponse> createDinosaur(@Valid @RequestBody CreateDinosaurRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dinosaurService.createDinosaur(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a dinosaur", description = "Updates an existing dinosaur")
    @ApiResponse(responseCode = "200", description = "Dinosaur updated successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DinosaurResponse.class)
            ))
    @ApiResponse(responseCode = "400",
            description = "Invalid input or trying to update an EXTINCT dinosaur",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    @ApiResponse(responseCode = "404",
            description = "Dinosaur not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    public ResponseEntity<DinosaurResponse> updateDinosaur(@Parameter(description = "Dinosaur ID", required = true, example = "69cc0a3b3ad0e659fad805db") @PathVariable String id, @Valid @RequestBody UpdateDinosaurRequest request) {
        return ResponseEntity.ok(dinosaurService.updateDinosaur(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a dinosaur", description = "Removes a dinosaur from the database")
    @ApiResponse(responseCode = "204", description = "Dinosaur deleted successfully")
    @ApiResponse(responseCode = "404",
            description = "Dinosaur not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    public ResponseEntity<Void> deleteDinosaur(@Parameter(description = "Dinosaur ID", required = true, example = "69cc0a3b3ad0e659fad805db") @PathVariable String id) {
        dinosaurService.deleteDinosaur(id);
        return ResponseEntity.noContent().build();
    }
}
