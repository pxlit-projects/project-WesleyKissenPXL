package be.pxl.services.services;

import be.pxl.services.controller.DTO.CommentResponseDTO;
import be.pxl.services.controller.request.CommentRequest;

import java.util.List;
import java.util.UUID;

public interface ICommentService {

    CommentResponseDTO addComment(CommentRequest comment, UUID postId);

    List<CommentResponseDTO> getAllCommentsFrom(UUID postId);
}
