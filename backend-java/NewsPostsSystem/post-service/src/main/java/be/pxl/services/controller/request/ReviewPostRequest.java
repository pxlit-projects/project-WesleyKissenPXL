package be.pxl.services.controller.request;

import be.pxl.services.domain.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewPostRequest {
    private UUID id;
    private String title;
    private String content;
    private String author;
    private LocalDateTime timeOfCreation;
    private Status status;
    private String rejectionReason;
}
