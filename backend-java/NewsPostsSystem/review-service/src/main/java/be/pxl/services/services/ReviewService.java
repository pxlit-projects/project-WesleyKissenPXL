package be.pxl.services.services;

import be.pxl.services.controller.dto.ReviewPostDTO;
import be.pxl.services.controller.request.PostStatusChangedRequest;
import be.pxl.services.controller.request.RejectionMessageRequest;
import be.pxl.services.controller.request.ReviewedPostRequest;
import be.pxl.services.domain.ReviewablePost;
import be.pxl.services.domain.Status;
import be.pxl.services.exception.ReviewException;
import be.pxl.services.feign.PostStatusChangedClient;
import be.pxl.services.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {
    private final ReviewRepository reviewRepository;
    private final RabbitTemplate rabbitTemplate;
    private final PostStatusChangedClient postStatusChangedClient;
    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);


    public void sendPostStatusSync(PostStatusChangedRequest postStatusChangedRequest) {
        log.info("Sending post status sync via Open Feign");
        postStatusChangedClient.sendChangesPostStatus(postStatusChangedRequest);
    }

    public void checkUserRoleHoofdAndRedac(String userRole){
        if(!userRole.equals("hoofdredacteur") && !userRole.equals("redacteur")) {
            throw new ReviewException("Invalid user role");
        }
    }

    @Override
    @RabbitListener(queues = "NieuweTestQue")
    @Transactional
    public void receiveFromGetApprovalQueue(ReviewPostDTO post) {
        log.info("Received approval request from NieuweTestQue");
        try {
            ReviewablePost review = ReviewablePost.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .author(post.getAuthor())
                    .status(post.getStatus())
                    .timeOfCreation(post.getTimeOfCreation())
                    .rejectionReason(post.getRejectionReason())
                    .build();

            reviewRepository.save(review);
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public ReviewPostDTO publishPost(UUID postId, String userRole) {
        if(!userRole.equals("hoofdredacteur")) {
            throw new ReviewException("Invalid user role");
        }
        log.info("Publishing post ID: " + postId);
        ReviewablePost post = reviewRepository.getReferenceById(postId);
        post.setStatus(Status.POSTED);
        reviewRepository.save(post);
        rabbitTemplate.convertAndSend("getApprovalAnswer", maptToReviewPostRequest(post));
        rabbitTemplate.convertAndSend("getApprovalAnswer", maptToReviewPostRequest(post));


        PostStatusChangedRequest request = PostStatusChangedRequest.builder()
                .postId(postId)
                .status(post.getStatus())
                .build();

        sendPostStatusSync(request);

        reviewRepository.deleteById(postId);

        return mapToReviewPostDTO(post);
    }

    @Override
    public List<ReviewPostDTO> getAllReviewablePosts(String userRole) {
        if(!userRole.equals("hoofdredacteur")) {
            throw new ReviewException("Invalid user role");
        }
        log.info("Getting all reviewable posts");
        return reviewRepository.findAll().stream().map(this::mapToReviewPostDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewPostDTO rejectPost(UUID postId, RejectionMessageRequest rejectionMessage, String userRole) {
        if(!userRole.equals("hoofdredacteur")) {
            throw new ReviewException("Invalid user role");
        }

        log.info("Rejecting post with ID: " + postId);
        ReviewablePost post = reviewRepository.getReferenceById(postId);

        if (rejectionMessage != null) {
            String message = rejectionMessage.getMessage();
            if (message != null && !message.isEmpty()) {
                post.setRejectionReason(message);
            }
        }

        post.setStatus(Status.REJECTED);

        PostStatusChangedRequest request = PostStatusChangedRequest.builder()
                .postId(postId)
                .status(post.getStatus())
                .build();

        sendPostStatusSync(request);

        rabbitTemplate.convertAndSend("getApprovalAnswer", maptToReviewPostRequest(post));
        rabbitTemplate.convertAndSend("getApprovalAnswer", maptToReviewPostRequest(post));

        reviewRepository.delete(post);
        return mapToReviewPostDTO(post);
    }


    private ReviewPostDTO mapToReviewPostDTO(ReviewablePost post) {
        log.info("Mapping review post to ReviewPostDTO");
        return ReviewPostDTO.builder()
                .id(post.getId()).title(post.getTitle()).content(post.getContent())
                .author(post.getAuthor()).timeOfCreation(post.getTimeOfCreation())
                .status(post.getStatus()).rejectionReason(post.getRejectionReason()).build();
    }

    private ReviewedPostRequest maptToReviewPostRequest(ReviewablePost post){
        log.info("Mapping review post to ReviewedPostRequest");
        ReviewedPostRequest reviewedPostRequest = ReviewedPostRequest.builder()
                .id(post.getId()).title(post.getTitle()).content(post.getContent())
                .timeOfCreation(post.getTimeOfCreation()).author(post.getAuthor())
                .status(post.getStatus()).rejectionReason(post.getRejectionReason()).build();
        return reviewedPostRequest;
    }
}
