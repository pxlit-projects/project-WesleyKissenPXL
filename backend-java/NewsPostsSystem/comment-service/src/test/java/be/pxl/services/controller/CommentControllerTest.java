package be.pxl.services.controller;

import be.pxl.services.controller.DTO.CommentResponseDTO;
import be.pxl.services.controller.request.CommentRequest;
import be.pxl.services.controller.request.UpdateCommentRequest;
import be.pxl.services.services.ICommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentControllerTest {

    @Mock
    private ICommentService commentService;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddComment() {
        UUID postId = UUID.randomUUID();
        String userRole = "ROLE_USER";
        CommentRequest commentRequest = new CommentRequest();
        CommentResponseDTO responseDTO = new CommentResponseDTO();

        when(commentService.addComment(commentRequest, postId, userRole)).thenReturn(responseDTO);

        ResponseEntity<CommentResponseDTO> response = commentController.addComment(userRole, postId, commentRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
        verify(commentService, times(1)).addComment(commentRequest, postId, userRole);
    }

    @Test
    void testGetComments() {
        UUID postId = UUID.randomUUID();
        String userRole = "ROLE_USER";
        List<CommentResponseDTO> comments = new ArrayList<>();

        when(commentService.getAllCommentsFrom(postId, userRole)).thenReturn(comments);

        ResponseEntity<List<CommentResponseDTO>> response = commentController.getComments(userRole, postId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(comments, response.getBody());
        verify(commentService, times(1)).getAllCommentsFrom(postId, userRole);
    }

    @Test
    void testGetAllComments() {
        String userRole = "ROLE_USER";
        List<CommentResponseDTO> comments = new ArrayList<>();

        when(commentService.getAllComments(userRole)).thenReturn(comments);

        ResponseEntity<List<CommentResponseDTO>> response = commentController.getAllComments(userRole);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(comments, response.getBody());
        verify(commentService, times(1)).getAllComments(userRole);
    }

    @Test
    void testChangeComment() {
        UUID commentId = UUID.randomUUID();
        String userRole = "ROLE_USER";
        UpdateCommentRequest updateRequest = new UpdateCommentRequest();
        CommentResponseDTO responseDTO = new CommentResponseDTO();

        when(commentService.changeComment(commentId, updateRequest, userRole)).thenReturn(responseDTO);

        ResponseEntity<CommentResponseDTO> response = commentController.changeComment(userRole, commentId, updateRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
        verify(commentService, times(1)).changeComment(commentId, updateRequest, userRole);
    }

    @Test
    void testDeleteComment() {
        UUID commentId = UUID.randomUUID();
        String userRole = "ROLE_USER";

        doNothing().when(commentService).deleteComment(commentId, userRole);

        ResponseEntity<String> response = commentController.deleteComment(userRole, commentId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Comment deleted successfully.", response.getBody());
        verify(commentService, times(1)).deleteComment(commentId, userRole);
    }
}
