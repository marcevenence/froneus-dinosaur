package com.froneus.dinosaur.dto;

import com.froneus.dinosaur.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DinosaurStatusMessage {
    private String dinosaurId;
    private Status newStatus;
    private LocalDateTime timestamp;
}
