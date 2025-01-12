package be.pxl.services.controller.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;
@Data
@Builder
public class CommentResponseDTO {
    private String comment;
    private String author;
    private UUID postId;
}
