package be.pxl.services;

import be.pxl.services.controller.DTO.CommentResponseDTO;
import be.pxl.services.controller.request.CommentRequest;
import be.pxl.services.domain.Comment;
import be.pxl.services.repository.CommentRepository;
import be.pxl.services.services.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.*;(UUID.class);
        import static org.mockito.Mockito.*;
        import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    private CommentRequest commentRequest;
    private UUID postId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setting up sample data
        commentRequest = new CommentRequest();
        commentRequest.setAuthor("test");
        commentRequest.setContent("test content");
        postId = UUID.randomUUID();
    }

    @Test
    void testAddComment() {
        // Arrange
        Comment newComment = Comment.builder()
                .id(UUID.randomUUID())
                .postId(postId)
                .author(commentRequest.getAuthor())
                .content(commentRequest.getContent())
                .build();

        when(commentRepository.save(any(Comment.class))).thenReturn(newComment);

        // Act
        CommentResponseDTO responseDTO = commentService.addComment(commentRequest, postId);

        // Assert
        assertNotNull(responseDTO);
        assertEquals(postId, responseDTO.getPostId());
        assertEquals(commentRequest.getAuthor(), responseDTO.getAuthor());
        assertEquals(commentRequest.getContent(), responseDTO.getComment());

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void testGetAllCommentsFrom() {
        // Arrange
        Comment comment1 = Comment.builder()
                .id(UUID.randomUUID())
                .postId(postId)
                .author("author1")
                .content("comment content 1")
                .build();
        Comment comment2 = Comment.builder()
                .id(UUID.randomUUID())
                .postId(postId)
                .author("author2")
                .content("comment content 2")
                .build();

        List<Comment> commentList = Arrays.asList(comment1, comment2);
        when(commentRepository.GetCommentsByPostId(postId)).thenReturn(commentList);

        // Act
        List<CommentResponseDTO> commentResponseList = commentService.getAllComments(postId);

        // Assert
        assertNotNull(commentResponseList);
        assertEquals(2, commentResponseList.size());

        CommentResponseDTO response1 = commentResponseList.get(0);
        assertEquals(comment1.getAuthor(), response1.getAuthor());
        assertEquals(comment1.getContent(), response1.getComment());
        assertEquals(postId, response1.getPostId());

        CommentResponseDTO response2 = commentResponseList.get(1);
        assertEquals(comment2.getAuthor(), response2.getAuthor());
        assertEquals(comment2.getContent(), response2.getComment());
        assertEquals(postId, response2.getPostId());

        verify(commentRepository, times(1)).GetCommentsByPostId(postId);
    }
}
