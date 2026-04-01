package com.froneus.dinosaur.dto;

import com.froneus.dinosaur.model.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Request object for updating an existing dinosaur")
public class UpdateDinosaurRequest {

    @Schema(description = "Unique name of the dinosaur", example = "Tyrannosaurus Rex")
    private String name;

    @Schema(description = "Species classification", example = "Theropod")
    private String species;

    @Schema(description = "Date when the dinosaur was discovered", example = "1902-01-01T23:59:59")
    private LocalDateTime discoveryDate;

    @Schema(description = "Estimated date of extinction", example = "2023-12-31T23:59:59")
    private LocalDateTime extinctionDate;

    @Schema(description = "Conservation status of the dinosaur", example = "ALIVE")
    private Status status;
}
