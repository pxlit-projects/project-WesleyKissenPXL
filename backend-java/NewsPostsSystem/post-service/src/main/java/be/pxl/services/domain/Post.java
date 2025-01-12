package be.pxl.services.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name= "post")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Post {
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
