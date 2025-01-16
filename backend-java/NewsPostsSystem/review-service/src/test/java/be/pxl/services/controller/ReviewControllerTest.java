package be.pxl.services.controller;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import be.pxl.services.controller.dto.ReviewPostDTO;
import be.pxl.services.controller.request.RejectionMessageRequest;
import be.pxl.services.services.IReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

class ReviewControllerTest {

    @Mock
    private IReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllReviewablePosts() {
        String userRole = "ROLE_USER";
        List<ReviewPostDTO> reviewPostDTOList = List.of(new ReviewPostDTO(), new ReviewPostDTO());

        // Mock the service method
        when(reviewService.getAllReviewablePosts(userRole)).thenReturn(reviewPostDTOList);

        // Call the controller method
        ResponseEntity<List<ReviewPostDTO>> response = reviewController.getAllReviewablePosts(userRole);

        // Assert the response
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(reviewPostDTOList, response.getBody());
        verify(reviewService, times(1)).getAllReviewablePosts(userRole);
    }

    @Test
    void testRejectPost() {
        UUID postId = UUID.randomUUID();
        String userRole = "ROLE_ADMIN";
        RejectionMessageRequest rejectionMessage = new RejectionMessageRequest();
        rejectionMessage.setMessage("ja das nie goed");
        ReviewPostDTO reviewPostDTO = new ReviewPostDTO();

        // Mock the service method
        when(reviewService.rejectPost(postId, rejectionMessage, userRole)).thenReturn(reviewPostDTO);

        // Call the controller method
        ResponseEntity<ReviewPostDTO> response = reviewController.reject(userRole, postId, rejectionMessage);

        // Assert the response
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(reviewPostDTO, response.getBody());
        verify(reviewService, times(1)).rejectPost(postId, rejectionMessage, userRole);
    }

    @Test
    void testPublishPost() {
        UUID postId = UUID.randomUUID();
        String userRole = "ROLE_ADMIN";
        ReviewPostDTO reviewPostDTO = new ReviewPostDTO();

        // Mock the service method
        when(reviewService.publishPost(postId, userRole)).thenReturn(reviewPostDTO);

        // Call the controller method
        ResponseEntity<ReviewPostDTO> response = reviewController.publish(userRole, postId);

        // Assert the response
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(reviewPostDTO, response.getBody());
        verify(reviewService, times(1)).publishPost(postId, userRole);
    }
}
