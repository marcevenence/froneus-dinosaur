package com.froneus.dinosaur.controller;

import com.froneus.dinosaur.dto.CreateDinosaurRequest;
import com.froneus.dinosaur.dto.UpdateDinosaurRequest;
import com.froneus.dinosaur.model.Dinosaur;
import com.froneus.dinosaur.service.DinosaurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dinosaur")
@RequiredArgsConstructor
public class DinosaurController {

    private final DinosaurService dinosaurService;

    @GetMapping
    public List<Dinosaur> getAllDinosaurs() {
        return dinosaurService.getAllDinosaurs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dinosaur> getDinosaurById(@PathVariable String id) {
        return ResponseEntity.ok(dinosaurService.getDinosaurById(id));
    }

    @PostMapping
    public ResponseEntity<Dinosaur> createDinosaur(@Valid @RequestBody CreateDinosaurRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dinosaurService.createDinosaur(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dinosaur> updateDinosaur(@PathVariable String id, @Valid @RequestBody UpdateDinosaurRequest request) {
        return ResponseEntity.ok(dinosaurService.updateDinosaur(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDinosaur(@PathVariable String id) {
        dinosaurService.deleteDinosaur(id);
        return ResponseEntity.noContent().build();
    }
}
