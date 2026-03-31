package com.froneus.dinosaur.dto;

import com.froneus.dinosaur.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateDinosaurRequest {
    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Species is mandatory")
    private String species;

    @NotNull(message = "Discovery date is mandatory")
    private LocalDateTime discoveryDate;

    @NotNull(message = "Extinction date is mandatory")
    private LocalDateTime extinctionDate;

    @NotNull(message = "Status is mandatory")
    private Status status;
}
