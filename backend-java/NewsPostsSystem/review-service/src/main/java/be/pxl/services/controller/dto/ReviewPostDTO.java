package be.pxl.services.controller.dto;

import be.pxl.services.domain.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ReviewPostDTO {
    //@JsonFormat(shape = JsonFormat.Shape.STRING)
    private UUID id;
    private String title;
    private String content;
    private String author;
    private LocalDateTime timeOfCreation;
    private Status status;
    private String rejectionReason;
}
