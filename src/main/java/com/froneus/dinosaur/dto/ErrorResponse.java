package com.froneus.dinosaur.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor; // Add this for a no-args constructor if needed

@Data
@NoArgsConstructor // Lombok will generate a no-args constructor
@Schema(description = "Response object for errors")
public class ErrorResponse {
    
    @NotBlank(message = "Message is mandatory")
    @Schema(description = "Error description", example = "Dinosaur error")
    private String message;

    // Custom constructor to set the message
    public ErrorResponse(String message) {
        this.message = message;
    }
}
