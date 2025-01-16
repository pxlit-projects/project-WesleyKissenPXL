package be.pxl.services.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import be.pxl.services.controller.dto.ReviewPostDTO;
import be.pxl.services.controller.request.PostStatusChangedRequest;
import be.pxl.services.controller.request.RejectionMessageRequest;
import be.pxl.services.domain.ReviewablePost;
import be.pxl.services.domain.Status;
import be.pxl.services.exception.ReviewException;
import be.pxl.services.feign.PostStatusChangedClient;
import be.pxl.services.repository.ReviewRepository;
import be.pxl.services.services.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private PostStatusChangedClient postStatusChangedClient;

    @InjectMocks
    private ReviewService reviewService;

    private UUID postId;
    private String userRole;
    private ReviewPostDTO postDTO;
    private RejectionMessageRequest rejectionMessage;
    private PostStatusChangedRequest postStatusChangedRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        postId = UUID.randomUUID();
        userRole = "hoofdredacteur";
        postDTO = new ReviewPostDTO(postId, "Post Title", "Post Content", "Author", null, Status.CONCEPT, null);

        rejectionMessage = new RejectionMessageRequest();
        rejectionMessage.setMessage("Reason for rejection");

        postStatusChangedRequest = PostStatusChangedRequest.builder()
                .postId(postId)
                .status(Status.REJECTED)
                .build();
    }

    @Test
    void testSendPostStatusSync() {

        reviewService.sendPostStatusSync(postStatusChangedRequest);

        verify(postStatusChangedClient, times(1)).sendChangesPostStatus(postStatusChangedRequest);
    }

    @Test
    void testCheckUserRoleHoofdAndRedac_ValidRole() {
        reviewService.checkUserRoleHoofdAndRedac("hoofdredacteur");
        reviewService.checkUserRoleHoofdAndRedac("redacteur");
    }

    @Test
    void testCheckUserRoleHoofdAndRedac_InvalidRole() {
        ReviewException exception = assertThrows(ReviewException.class, () -> reviewService.checkUserRoleHoofdAndRedac("gebruiker"));
        assertEquals("Invalid user role", exception.getMessage());
    }

    @Test
    @Transactional
    void testReceiveFromGetApprovalQueue() {
        ReviewablePost reviewablePost = new ReviewablePost(postId, "Post Title", "Post Content", "Author", null,Status.CONCEPT, null);
        when(reviewRepository.save(any())).thenReturn(reviewablePost);

        reviewService.receiveFromGetApprovalQueue(postDTO);

        verify(reviewRepository, times(1)).save(any(ReviewablePost.class));
    }

    @Test
    void testPublishPost_ValidRole() {
        ReviewablePost reviewablePost = new ReviewablePost(postId, "Post Title", "Post Content", "Author",  null,Status.CONCEPT, null);
        when(reviewRepository.getReferenceById(postId)).thenReturn(reviewablePost);
        when(reviewRepository.save(any(ReviewablePost.class))).thenReturn(reviewablePost);
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), any(Object.class));

        ReviewPostDTO response = reviewService.publishPost(postId, userRole);

        assertNotNull(response);
        verify(reviewRepository).getReferenceById(postId);
        verify(reviewRepository).save(any(ReviewablePost.class));
        verify(rabbitTemplate, times(2)).convertAndSend(anyString(), any(Object.class));
        verify(postStatusChangedClient).sendChangesPostStatus(any());
    }

    @Test
    void testPublishPost_InvalidRole() {
        userRole = "redacteur";
        ReviewException exception = assertThrows(ReviewException.class, () -> reviewService.publishPost(postId, userRole));
        assertEquals("Invalid user role", exception.getMessage());
    }

    @Test
    void testGetAllReviewablePosts_ValidRole() {
        List<ReviewablePost> reviewablePosts = List.of(new ReviewablePost(postId, "Post Title", "Post Content", "Author", null,Status.CONCEPT, null));
        when(reviewRepository.findAll()).thenReturn(reviewablePosts);

        List<ReviewPostDTO> response = reviewService.getAllReviewablePosts(userRole);

        assertNotNull(response);
        verify(reviewRepository, times(1)).findAll();
    }

    @Test
    void testGetAllReviewablePosts_InvalidRole() {
        userRole = "redacteur";
        ReviewException exception = assertThrows(ReviewException.class, () -> reviewService.getAllReviewablePosts(userRole));
        assertEquals("Invalid user role", exception.getMessage());
    }

    @Test
    void testRejectPost_ValidRole() {
        ReviewablePost reviewablePost = new ReviewablePost(postId, "Post Title", "Post Content", "Author", null,Status.CONCEPT, null);
        when(reviewRepository.getReferenceById(postId)).thenReturn(reviewablePost);
        when(reviewRepository.save(any(ReviewablePost.class))).thenReturn(reviewablePost);
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), any(Object.class));


        ReviewPostDTO response = reviewService.rejectPost(postId, rejectionMessage, userRole);

        assertNotNull(response);
        verify(reviewRepository).getReferenceById(postId);
        verify(rabbitTemplate, times(2)).convertAndSend(anyString(),any(Object.class));
        verify(postStatusChangedClient).sendChangesPostStatus(any());
    }

    @Test
    void testRejectPost_InvalidRole() {
        userRole = "redacteur";
        ReviewException exception = assertThrows(ReviewException.class, () -> reviewService.rejectPost(postId, rejectionMessage, userRole));
        assertEquals("Invalid user role", exception.getMessage());
    }

    @Test
    void testRejectPost_NoMessage() {
        RejectionMessageRequest rejectionMessage = new RejectionMessageRequest();
        rejectionMessage.setMessage("test");
        ReviewablePost reviewablePost = new ReviewablePost(postId, "Post Title", "Post Content", "Author", null,Status.CONCEPT, null);
        when(reviewRepository.getReferenceById(postId)).thenReturn(reviewablePost);
        when(reviewRepository.save(any(ReviewablePost.class))).thenReturn(reviewablePost);
        doNothing().when(rabbitTemplate).convertAndSend(anyString(),any(Object.class));


        ReviewPostDTO response = reviewService.rejectPost(postId, rejectionMessage, userRole);

        assertNotNull(response);
        verify(reviewRepository).getReferenceById(postId);
        verify(rabbitTemplate, times(2)).convertAndSend(anyString(), any(Object.class));
        verify(postStatusChangedClient).sendChangesPostStatus(any());
    }
}