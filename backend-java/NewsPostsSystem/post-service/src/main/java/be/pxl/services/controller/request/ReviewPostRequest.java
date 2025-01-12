package be.pxl.services.controller.request;

import be.pxl.services.domain.Status;
import jakarta.persistence.*;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public class ReviewPostRequest {
    @Id
    private UUID id;
    private String title;
    private String content;
    private String author;
    private LocalDateTime timeOfCreation;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String rejectionReason;
}
