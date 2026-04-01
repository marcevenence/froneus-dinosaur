package com.froneus.dinosaur.dto;

import com.froneus.dinosaur.model.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Request object for creating a new dinosaur")
public class DinosaurResponse {

    @NotBlank(message = "ID is mandatory")
    @Schema(description = "Id of the dinosaur", example = "69cc0a3b3ad0e659fad805db")
    private String id;

    @NotBlank(message = "Name is mandatory")
    @Schema(description = "Unique name of the dinosaur", example = "Tyrannosaurus Rex")
    private String name;

    @NotBlank(message = "Species is mandatory")
    @Schema(description = "Species classification", example = "Theropod")
    private String species;

    @NotNull(message = "Discovery date is mandatory")
    @Schema(description = "Date when the dinosaur was discovered", example = "1902-01-01T23:59:59")
    private LocalDateTime discoveryDate;

    @NotNull(message = "Extinction date is mandatory")
    @Schema(description = "Estimated date of extinction", example = "2023-12-31T23:59:59")
    private LocalDateTime extinctionDate;

    @Schema(description = "Conservation status of the dinosaur", example = "ALIVE")
    private Status status;
}
