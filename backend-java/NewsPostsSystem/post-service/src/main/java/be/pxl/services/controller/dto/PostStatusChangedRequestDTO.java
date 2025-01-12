package be.pxl.services.controller.dto;

import be.pxl.services.domain.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostStatusChangedRequestDTO {
    private UUID postId;
    private Status status;
}
