package be.pxl.services.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name= "notification")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Notification {
    @Id
    private UUID id;
    private UUID postId;
    @Enumerated(EnumType.STRING)
    private Status status;
}
