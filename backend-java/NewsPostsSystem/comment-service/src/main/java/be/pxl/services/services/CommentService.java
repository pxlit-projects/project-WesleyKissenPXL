package be.pxl.services.services;

import be.pxl.services.controller.DTO.CommentResponseDTO;
import be.pxl.services.controller.request.CommentRequest;
import be.pxl.services.controller.request.UpdateCommentRequest;
import be.pxl.services.domain.Comment;
import be.pxl.services.exceptions.CommentException;
import be.pxl.services.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {
    private final CommentRepository commentRepository;
    private static final Logger log = LoggerFactory.getLogger(CommentService.class);


    public void checkUserRoleAllUsers(String userRole){
        if(!userRole.equals("hoofdredacteur") && !userRole.equals("redacteur") && !userRole.equals("gebruiker")) {
            throw new CommentException("Invalid user role");
        }
    }

    @Override
    public CommentResponseDTO addComment(CommentRequest comment, UUID postId, String userRole) {
        checkUserRoleAllUsers(userRole);
        log.info("Adding comment to post with postId: {}", postId);
        Comment newComment = Comment.builder().id(generateId()).postId(postId).author(comment.getAuthor()).content(comment.getContent()).build();
        commentRepository.save(newComment);

        return CommentResponseDTO.builder().postId(postId).comment(newComment.getContent()).author(newComment.getAuthor()).build();
    }

    @Override
    public List<CommentResponseDTO> getAllCommentsFrom(UUID postId, String userRole) {
        checkUserRoleAllUsers(userRole);
        log.info("Getting all comments from post with postId: {}", postId);
        List<Comment> comments = commentRepository.findByPostId(postId);

        return comments.stream().map(this::mapToCommentResponseDTO).collect(Collectors.toList());
    }

    @Override
    public CommentResponseDTO changeComment(UUID id, UpdateCommentRequest commentRequest, String userRole) {
        checkUserRoleAllUsers(userRole);
        log.info("Updating comment with id: {}", id);
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentException("Comment not found with id: " + id));

        String newContent = commentRequest.getContent();
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new CommentException("Comment content cannot be empty");
        }

        comment.setContent(newContent);

        commentRepository.save(comment);

        return mapToCommentResponseDTO(comment);
    }

    public void deleteComment(UUID id, String userRole) {
        checkUserRoleAllUsers(userRole);
        log.info("Deleting comment with id: {}", id);
        if (!commentRepository.existsById(id)) {
            throw new CommentException("Comment not found with ID: " + id);
        }
        Comment comment = commentRepository.getById(id);
        commentRepository.delete(comment);
    }

    @Override
    public List<CommentResponseDTO> getAllComments(String userRole) {
        checkUserRoleAllUsers(userRole);
        log.info("Getting all comments");
        List<Comment> comments = commentRepository.findAll();

        return comments.stream().map(this::mapToCommentResponseDTO).collect(Collectors.toList());
    }


    private CommentResponseDTO mapToCommentResponseDTO(Comment comment) {
        log.info("Converting comment to CommentResponseDTO: {}", comment);
        return CommentResponseDTO.builder().postId(comment.getPostId()).author(comment.getAuthor()).comment(comment.getContent()).id(comment.getId()).build();
    }

    private UUID generateId(){
        log.info("Generating new UUID");
        return UUID.randomUUID();
    }
}
