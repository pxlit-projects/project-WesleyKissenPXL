package be.pxl.services.controller.request;

import be.pxl.services.domain.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public class ReviewedPostRequest {
    private UUID id;
    private String title;
    private String content;
    private String author;
    private LocalDateTime timeOfCreation;
    private Status status;
    private String rejectionReason;
}
