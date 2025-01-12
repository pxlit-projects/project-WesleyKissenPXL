package be.pxl.services.services;

import be.pxl.services.controller.dto.PostResponse;
import be.pxl.services.controller.dto.PostStatusChangedRequestDTO;
import be.pxl.services.controller.dto.ReviewPostDTO;
import be.pxl.services.controller.request.CreatePostRequest;
import be.pxl.services.controller.request.ReviewPostRequest;
import be.pxl.services.controller.request.UpdatePostRequest;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.ReviewablePost;
import be.pxl.services.domain.Status;
import be.pxl.services.exception.PostException;
import be.pxl.services.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService{
    private final PostRepository postRepository;
    private final List<PostStatusChangedRequestDTO> postStatusRequests = new ArrayList<>();
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public PostResponse addPost(CreatePostRequest createPostRequest) {
        Post post = Post.builder()
                .id(generateId())
                .title(createPostRequest.getTitle())
                .content(createPostRequest.getContent())
                .author(createPostRequest.getAuthor())
                .timeOfCreation(LocalDateTime.now())
                .status(Status.WAITING_FOR_APPROVEL)
                .build();

        postRepository.save(post);

        ReviewPostRequest reviewPostRequest = ReviewPostRequest.builder()
                        .id(post.getId()).title(post.getTitle()).content(post.getContent())
                        .author(post.getAuthor()).timeOfCreation(post.getTimeOfCreation()).status(post.getStatus())
                .rejectionReason(post.getRejectionReason()).build();

        rabbitTemplate.convertAndSend("getApproval", reviewPostRequest);



        return mapToPostResponse(post);
    }

    @Override
    public PostResponse changePost(UpdatePostRequest updatePostRequest, UUID id) {
        if (updatePostRequest == null || updatePostRequest.getTitle() == null || updatePostRequest.getContent() == null) {
            throw new PostException("Invalid update request: title and content must not be null.");
        }

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostException("Post with id " + id + " not found"));

        validatePostStatus(post);

        updatePostFields(post, updatePostRequest);

        if (!post.getTitle().equals(updatePostRequest.getTitle()) || !post.getContent().equals(updatePostRequest.getContent())) {
            postRepository.save(post);
        }
        return mapToPostResponse(post);
    }

    @Override
    public List<PostResponse> getAllPostedPosts() {
        return postRepository.getPostsByStatus(Status.POSTED).stream()
                .map(this::mapToPostResponse)
                .collect(Collectors.toList());
    }

    private void validatePostStatus(Post post) {
        if (post.getStatus() != Status.CONCEPT) {
            throw new PostException("Post with id " + post.getId() + " is not changeable anymore.");
        }
    }

    private void updatePostFields(Post post, UpdatePostRequest updatePostRequest) {
        post.setTitle(updatePostRequest.getTitle());
        post.setContent(updatePostRequest.getContent());
    }


    private PostResponse mapToPostResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId()).title(post.getTitle()).content(post.getContent())
                .author(post.getAuthor()).timeOfCreation(post.getTimeOfCreation())
                .status(post.getStatus()).build();
    }

    private UUID generateId(){
        return UUID.randomUUID();
    }

    @Override
    public List<PostResponse> filterPosts(String content, String author, String fromDate, String toDate) { //2025-01-12T10:15:30
        List<Post> filteredPosts = postRepository.findAll();

        if (content != null && !content.isEmpty()) {
            filteredPosts = filteredPosts.stream()
                    .filter(post -> post.getContent() != null && post.getContent().contains(content))
                    .collect(Collectors.toList());
        }

        if (author != null && !author.isEmpty()) {
            filteredPosts = filteredPosts.stream()
                    .filter(post -> post.getAuthor() != null && post.getAuthor().equalsIgnoreCase(author))
                    .collect(Collectors.toList());
        }

        if (fromDate != null && toDate != null) {
            LocalDateTime from = LocalDateTime.parse(fromDate);
            LocalDateTime to = LocalDateTime.parse(toDate);

            filteredPosts = filteredPosts.stream()
                    .filter(post -> post.getTimeOfCreation() != null &&
                            !post.getTimeOfCreation().isBefore(from) &&
                            !post.getTimeOfCreation().isAfter(to))
                    .collect(Collectors.toList());
        }

        return filteredPosts.stream()
                .map(this::mapToPostResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PostResponse addPostAsConcept(CreatePostRequest createPostRequest) {
        Post post = Post.builder()
                .title(createPostRequest.getTitle())
                .content(createPostRequest.getContent())
                .author(createPostRequest.getAuthor())
                .timeOfCreation(LocalDateTime.now())
                .status(Status.CONCEPT)
                .build();

        postRepository.save(post);
        return mapToPostResponse(post);
    }

    @Override
    public void saveNotifications(PostStatusChangedRequestDTO request) {
        postStatusRequests.add(request);
    }

    @Override
    public List<PostStatusChangedRequestDTO> GetAllNotifications() {
        return postStatusRequests;
    }


    @RabbitListener(queues = "getApprovalAnswer")
    public void receiveFromGetApprovalAnswerQueue(ReviewPostDTO post) {
        Post reviewedPost = Post.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getAuthor())
                .status(post.getStatus())
                .timeOfCreation(post.getTimeOfCreation())
                .rejectionReason(post.getRejectionReason())
                .build();

//        if (postRepository.existsById(review.getId())) {
//            log.info("Updating existing review with id: {}", post.getId());
//        } else {
//            log.info("Saving new review with id: {}", post.getId());
//        }

        postRepository.save(reviewedPost);
    }

}
