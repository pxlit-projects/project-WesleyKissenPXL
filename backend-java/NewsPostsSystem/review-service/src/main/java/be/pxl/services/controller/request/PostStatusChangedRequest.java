package be.pxl.services.controller.request;

import be.pxl.services.domain.Status;
import lombok.*;

import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PostStatusChangedRequest {
    private UUID postId;
    private Status status;
}
