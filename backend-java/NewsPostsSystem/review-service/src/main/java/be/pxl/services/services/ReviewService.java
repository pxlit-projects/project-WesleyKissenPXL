package be.pxl.services.services;

import be.pxl.services.controller.dto.ReviewPostDTO;
import be.pxl.services.controller.request.RejectionMessageRequest;
import be.pxl.services.controller.request.ReviewedPostRequest;
import be.pxl.services.domain.ReviewablePost;
import be.pxl.services.domain.Status;
import be.pxl.services.feign.PostStatusChangedClient;
import be.pxl.services.controller.request.PostStatusChangedRequest;
import be.pxl.services.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {
    private final ReviewRepository reviewRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private final PostStatusChangedClient  postStatusChangedClient;

    public void sendPostStatusSync(PostStatusChangedRequest postStatusChangedRequest) {
        postStatusChangedClient.sendChangesPostStatus(postStatusChangedRequest);
    }

    @RabbitListener(queues = "getApproval")
    public void receiveFromGetApprovalQueue(ReviewPostDTO post) {
        ReviewablePost review = ReviewablePost.builder().id(post.getId()).title(post.getTitle()).content(post.getContent())
                        .author(post.getAuthor()).status(post.getStatus()).timeOfCreation(post.getTimeOfCreation())
                .rejectionReason(post.getRejectionReason()).build();

        reviewRepository.save(review);

        ReviewedPostRequest reviewedPostRequest = ReviewedPostRequest.builder()
                        .id(review.getId()).title(review.getTitle()).content(review.getContent())
                        .timeOfCreation(review.getTimeOfCreation()).author(review.getAuthor())
                        .status(review.getStatus()).rejectionReason(review.getRejectionReason()).build();

        rabbitTemplate.convertAndSend("getApprovalAnswer", reviewedPostRequest);
    }

    @Override
    public List<ReviewPostDTO> getAllReviewablePosts() {
        return reviewRepository.findAll().stream().map(this::mapToReviewPostDTO)
                .collect(Collectors.toList());



    }

    @Override
    public ReviewPostDTO rejectPost(UUID postId, RejectionMessageRequest rejectionMessage) {
        ReviewablePost post = reviewRepository.getReferenceById(postId);
        if (!rejectionMessage.getMessage().isEmpty()){
            post.setRejectionReason(rejectionMessage.getMessage());
        }
        post.setStatus(Status.REJECTED);

        reviewRepository.save(post);

        PostStatusChangedRequest request = PostStatusChangedRequest.builder().postId(postId).status(post.getStatus()).build();

        sendPostStatusSync(request);

        return mapToReviewPostDTO(post);
    }


    private ReviewPostDTO mapToReviewPostDTO(ReviewablePost post) {
        return ReviewPostDTO.builder()
                .id(post.getId()).title(post.getTitle()).content(post.getContent())
                .author(post.getAuthor()).timeOfCreation(post.getTimeOfCreation())
                .status(post.getStatus()).rejectionReason(post.getRejectionReason()).build();
    }
}
