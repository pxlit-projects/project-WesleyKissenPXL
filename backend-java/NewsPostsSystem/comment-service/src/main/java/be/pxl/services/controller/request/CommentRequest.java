package be.pxl.services.controller.request;

import lombok.Data;

import java.util.UUID;
@Data
public class CommentRequest {
    private String content;
    private String author;
}
