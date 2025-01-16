package be.pxl.services.services;


import be.pxl.services.controller.DTO.CommentResponseDTO;
import be.pxl.services.controller.request.CommentRequest;
import be.pxl.services.controller.request.UpdateCommentRequest;
import be.pxl.services.domain.Comment;
import be.pxl.services.exceptions.CommentException;
import be.pxl.services.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckUserRoleAllUsers_ValidRoles() {
        assertDoesNotThrow(() -> commentService.checkUserRoleAllUsers("hoofdredacteur"));
        assertDoesNotThrow(() -> commentService.checkUserRoleAllUsers("redacteur"));
        assertDoesNotThrow(() -> commentService.checkUserRoleAllUsers("gebruiker"));
    }

    @Test
    void testCheckUserRoleAllUsers_InvalidRole() {
        assertThrows(CommentException.class, () -> commentService.checkUserRoleAllUsers("invalidRole"));
    }

    @Test
    void testAddComment() {
        UUID postId = UUID.randomUUID();
        CommentRequest request = new CommentRequest("This is a test comment.", "Author");
        Comment comment = Comment.builder()
                .id(UUID.randomUUID())
                .postId(postId)
                .author("Author")
                .content("This is a test comment.")
                .build();

        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentResponseDTO response = commentService.addComment(request, postId, "gebruiker");

        assertNotNull(response);
        assertEquals("Author", response.getAuthor());
        assertEquals("This is a test comment.", response.getComment());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void testAddComment_InvalidRole() {
        UUID postId = UUID.randomUUID();
        CommentRequest request = new CommentRequest("Author", "This is a test comment.");

        assertThrows(CommentException.class, () -> commentService.addComment(request, postId, "invalidRole"));
    }

    @Test
    void testGetAllCommentsFrom() {
        UUID postId = UUID.randomUUID();
        Comment comment = Comment.builder()
                .id(UUID.randomUUID())
                .postId(postId)
                .author("Author")
                .content("Test content")
                .build();

        when(commentRepository.findByPostId(postId)).thenReturn(Collections.singletonList(comment));

        List<CommentResponseDTO> comments = commentService.getAllCommentsFrom(postId, "gebruiker");

        assertEquals(1, comments.size());
        assertEquals("Author", comments.get(0).getAuthor());
        assertEquals("Test content", comments.get(0).getComment());
        verify(commentRepository, times(1)).findByPostId(postId);
    }

    @Test
    void testChangeComment() {
        UUID commentId = UUID.randomUUID();
        UpdateCommentRequest request = new UpdateCommentRequest("Updated content");
        Comment comment = Comment.builder()
                .id(commentId)
                .postId(UUID.randomUUID())
                .author("Author")
                .content("Old content")
                .build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentResponseDTO response = commentService.changeComment(commentId, request, "gebruiker");

        assertEquals("Updated content", response.getComment());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void testChangeComment_NotFound() {
        UUID commentId = UUID.randomUUID();
        UpdateCommentRequest request = new UpdateCommentRequest("Updated content");

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(CommentException.class, () -> commentService.changeComment(commentId, request, "gebruiker"));
    }

    @Test
    void testChangeComment_InvalidContent() {
        UUID commentId = UUID.randomUUID();
        UpdateCommentRequest request = new UpdateCommentRequest("");
        Comment comment = Comment.builder()
                .id(commentId)
                .postId(UUID.randomUUID())
                .author("Author")
                .content("Old content")
                .build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        assertThrows(CommentException.class, () -> commentService.changeComment(commentId, request, "gebruiker"));
    }

    @Test
    void testDeleteComment() {
        UUID commentId = UUID.randomUUID();
        Comment comment = Comment.builder()
                .id(commentId)
                .postId(UUID.randomUUID())
                .author("Author")
                .content("Content to delete")
                .build();

        when(commentRepository.existsById(commentId)).thenReturn(true);
        when(commentRepository.getById(commentId)).thenReturn(comment);

        assertDoesNotThrow(() -> commentService.deleteComment(commentId, "gebruiker"));
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void testDeleteComment_NotFound() {
        UUID commentId = UUID.randomUUID();

        when(commentRepository.existsById(commentId)).thenReturn(false);

        assertThrows(CommentException.class, () -> commentService.deleteComment(commentId, "gebruiker"));
    }

    @Test
    void testGetAllComments() {
        Comment comment = Comment.builder()
                .id(UUID.randomUUID())
                .postId(UUID.randomUUID())
                .author("Author")
                .content("Content")
                .build();

        when(commentRepository.findAll()).thenReturn(Collections.singletonList(comment));

        List<CommentResponseDTO> comments = commentService.getAllComments("gebruiker");

        assertEquals(1, comments.size());
        assertEquals("Author", comments.get(0).getAuthor());
        assertEquals("Content", comments.get(0).getComment());
        verify(commentRepository, times(1)).findAll();
    }
}
