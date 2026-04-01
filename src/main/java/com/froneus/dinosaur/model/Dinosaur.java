package com.froneus.dinosaur.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "dinosaurs")
public class Dinosaur {

    @Id
    private String id;

    @NotBlank(message = "Name is mandatory")
    @Indexed(unique = true)
    private String name;

    @NotBlank(message = "Species is mandatory")
    private String species;

    @NotNull(message = "Discovery date is mandatory")
    private LocalDateTime discoveryDate;

    @NotNull(message = "ExtinctionDate date is mandatory")
    @Indexed
    private LocalDateTime extinctionDate;

    @NotNull(message = "Status is mandatory")
    private Status status;
}
