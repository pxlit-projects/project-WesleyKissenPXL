package be.pxl.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import be.pxl.services.controller.dto.ReviewPostDTO;
import be.pxl.services.controller.request.RejectionMessageRequest;
import be.pxl.services.controller.request.PostStatusChangedRequest;
import be.pxl.services.domain.ReviewablePost;
import be.pxl.services.domain.Status;
import be.pxl.services.feign.PostStatusChangedClient;
import be.pxl.services.repository.ReviewRepository;
import be.pxl.services.services.ReviewService;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
    private ReviewablePost reviewablePost;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        postId = UUID.randomUUID();
        reviewablePost = ReviewablePost.builder()
                .id(postId)
                .title("Post Title")
                .content("Post Content")
                .author("Post Author")
                .status(Status.WAITING_FOR_APPROVEL)
                .timeOfCreation(null)
                .rejectionReason(null)
                .build();
    }

    @Test
    void testReceiveFromGetApprovalQueue() {
        ReviewPostDTO reviewPostDTO = new ReviewPostDTO(postId, "Post Title", "Post Content", "Post Author", LocalDateTime.now() ,Status.WAITING_FOR_APPROVEL, null);

        when(reviewRepository.save(any(ReviewablePost.class))).thenReturn(reviewablePost);

        reviewService.receiveFromGetApprovalQueue(reviewPostDTO);

        verify(reviewRepository, times(1)).save(any(ReviewablePost.class));  // Ensure save is called
    }

    @Test
    void testGetAllReviewablePosts() {
        when(reviewRepository.findAll()).thenReturn(List.of(reviewablePost));

        List<ReviewPostDTO> reviewPostDTOList = reviewService.getAllReviewablePosts();

        assertNotNull(reviewPostDTOList);
        assertEquals(1, reviewPostDTOList.size());
        assertEquals("Post Title", reviewPostDTOList.get(0).getTitle());
    }

    @Test
    void testRejectPost() {
        RejectionMessageRequest rejectionMessageRequest = new RejectionMessageRequest();
        rejectionMessageRequest.setMessage("Rejection reason");

        when(reviewRepository.getReferenceById(postId)).thenReturn(reviewablePost);
        when(reviewRepository.save(any(ReviewablePost.class))).thenReturn(reviewablePost);

        ReviewPostDTO rejectedPostDTO = reviewService.rejectPost(postId, rejectionMessageRequest);

        assertNotNull(rejectedPostDTO);
        assertEquals(Status.REJECTED, rejectedPostDTO.getStatus());
        assertEquals("Rejection reason", rejectedPostDTO.getRejectionReason());

        // Verify that the status was updated and saved
        verify(reviewRepository, times(1)).save(any(ReviewablePost.class));

        // Ensure the PostStatusChangedClient's sendChangesPostStatus method is called
        PostStatusChangedRequest postStatusChangedRequest = new PostStatusChangedRequest(postId, Status.REJECTED);
        verify(postStatusChangedClient, times(1)).sendChangesPostStatus(postStatusChangedRequest);
    }

    @Test
    void testRejectPostWithoutMessage() {
        RejectionMessageRequest rejectionMessageRequest = new RejectionMessageRequest(); // Empty rejection message

        when(reviewRepository.getReferenceById(postId)).thenReturn(reviewablePost);
        when(reviewRepository.save(any(ReviewablePost.class))).thenReturn(reviewablePost);

        ReviewPostDTO rejectedPostDTO = reviewService.rejectPost(postId, rejectionMessageRequest);

        assertNotNull(rejectedPostDTO);
        assertEquals(Status.REJECTED, rejectedPostDTO.getStatus());
        assertNull(rejectedPostDTO.getRejectionReason());

        verify(reviewRepository, times(1)).save(any(ReviewablePost.class));
        verify(postStatusChangedClient, times(1)).sendChangesPostStatus(any(PostStatusChangedRequest.class));
    }

    @Test
    void testSendPostStatusSync() {
        PostStatusChangedRequest postStatusChangedRequest = new PostStatusChangedRequest(postId, Status.POSTED);

        reviewService.sendPostStatusSync(postStatusChangedRequest);

        // Verify that sendChangesPostStatus was called with the correct argument
        verify(postStatusChangedClient, times(1)).sendChangesPostStatus(postStatusChangedRequest);
    }
}
