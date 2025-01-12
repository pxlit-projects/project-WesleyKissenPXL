package be.pxl.services.services;

import be.pxl.services.controller.DTO.CommentResponseDTO;
import be.pxl.services.controller.request.CommentRequest;
import be.pxl.services.domain.Comment;
import be.pxl.services.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {
    private final CommentRepository commentRepository;

    @Override
    public CommentResponseDTO addComment(CommentRequest comment, UUID postId) {
        Comment newComment = Comment.builder().id(generateId()).postId(postId).author(comment.getAuthor()).content(comment.getContent()).build();
        commentRepository.save(newComment);

        return CommentResponseDTO.builder().postId(postId).comment(newComment.getContent()).author(newComment.getAuthor()).build();
    }

    @Override
    public List<CommentResponseDTO> getAllCommentsFrom(UUID postId) {
        List<Comment> comments = commentRepository.GetCommentsByPostId(postId);

        return comments.stream().map(this::mapToCommentResponseDTO).collect(Collectors.toList());
    }

    private CommentResponseDTO mapToCommentResponseDTO(Comment comment) {
        return CommentResponseDTO.builder().postId(comment.getPostId()).author(comment.getAuthor()).comment(comment.getContent()).build();
    }

    private UUID generateId(){
        return UUID.randomUUID();
    }


}
