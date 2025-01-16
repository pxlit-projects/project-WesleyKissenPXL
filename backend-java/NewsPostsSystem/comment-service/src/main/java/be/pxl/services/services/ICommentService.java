package be.pxl.services.services;

import be.pxl.services.controller.DTO.CommentResponseDTO;
import be.pxl.services.controller.request.CommentRequest;
import be.pxl.services.controller.request.UpdateCommentRequest;

import java.util.List;
import java.util.UUID;

public interface ICommentService {

    CommentResponseDTO addComment(CommentRequest comment, UUID postId, String userRole);

    List<CommentResponseDTO> getAllCommentsFrom(UUID postId, String userRole);

    CommentResponseDTO changeComment(UUID id, UpdateCommentRequest comment, String userRole);

    void deleteComment(UUID id, String userRole);

    List<CommentResponseDTO> getAllComments(String userRole);
}

