package be.pxl.services.services;

import be.pxl.services.controller.dto.NotificationsDTO;
import be.pxl.services.controller.dto.PostResponse;
import be.pxl.services.controller.dto.PostStatusChangedRequestDTO;
import be.pxl.services.controller.dto.ReviewPostDTO;
import be.pxl.services.controller.request.CreatePostRequest;
import be.pxl.services.controller.request.ReviewPostRequest;
import be.pxl.services.controller.request.UpdatePostRequest;
import be.pxl.services.domain.Notification;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.Status;
import be.pxl.services.exception.PostException;
import be.pxl.services.repository.NotificationRepository;
import be.pxl.services.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService{
    private final PostRepository postRepository;
    private final NotificationRepository notificationRepository;
    private final RabbitTemplate rabbitTemplate;
    private static final Logger log = LoggerFactory.getLogger(PostService.class);



    public void checkUserRoleHoofdAndRedac(String userRole){
        if(!userRole.equals("hoofdredacteur") && !userRole.equals("redacteur")) {
            throw new PostException("Invalid user role");
        }
    }
    public void checkUserRoleAllUsers(String userRole){
        if(!userRole.equals("hoofdredacteur") && !userRole.equals("redacteur") && !userRole.equals("gebruiker")) {
            throw new PostException("Invalid user role");
        }
    }

    @Override
    @Transactional
    public PostResponse addPost(CreatePostRequest createPostRequest, String userRole) {
        checkUserRoleHoofdAndRedac(userRole);

        log.info("Adding new post: {}", createPostRequest.getTitle());
        Post post = Post.builder()
                .id(generateId())
                .title(createPostRequest.getTitle())
                .content(createPostRequest.getContent())
                .author(createPostRequest.getAuthor())
                .timeOfCreation(LocalDateTime.now())
                .status(Status.WAITING_FOR_APPROVEL)
                .build();

        ReviewPostRequest reviewPostRequest = ReviewPostRequest.builder()
                .id(post.getId()).title(post.getTitle()).content(post.getContent())
                .author(post.getAuthor()).timeOfCreation(post.getTimeOfCreation()).status(post.getStatus())
                .rejectionReason(post.getRejectionReason()).build();

        rabbitTemplate.convertAndSend("NieuweTestQue", reviewPostRequest);
        rabbitTemplate.convertAndSend("NieuweTestQue", reviewPostRequest);
        log.info("Send post to reviewService: {}", post);

        postRepository.save(post);
        log.info("Saved post: {}", post);
        return mapToPostResponse(post);
    }

    @Override
    public PostResponse changePost(UpdatePostRequest updatePostRequest, UUID id, String userRole) {
        checkUserRoleHoofdAndRedac(userRole);
        log.info("Changing post: {}", id);
        validateUpdatePostRequest(updatePostRequest);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostException("Post with id " + id + " not found"));
        log.info("Post found: {}", post);

        validatePostStatus(post);

        updatePostFields(post, updatePostRequest);

        postRepository.save(post);
        log.info("Saved post: {}", post);
        return mapToPostResponse(post);
    }

    @Override
    public List<PostResponse> getAllPostedPosts(String userRole) {
        checkUserRoleAllUsers(userRole);
        log.info("Getting all posted posts");
        return postRepository.getPostsByStatus(Status.POSTED).stream()
                .map(this::mapToPostResponse)
                .collect(Collectors.toList());
    }

    private void validatePostStatus(Post post) {
        log.info("Validating post status: {}", post);
        if (post.getStatus() != Status.CONCEPT && post.getStatus() != Status.REJECTED) {
            throw new PostException("Post with id " + post.getId() + " is not changeable anymore.");
        }
    }

    private void updatePostFields(Post post, UpdatePostRequest updatePostRequest) {
        log.info("Updating post fields: {}", updatePostRequest);
        post.setTitle(updatePostRequest.getTitle());
        post.setContent(updatePostRequest.getContent());
        post.setAuthor(updatePostRequest.getAuthor());
    }

    private void updatePostFieldsAndSendForReview(Post post, UpdatePostRequest updatePostRequest) {
        log.info("Updating post fields and sending for review: {}", updatePostRequest);
        post.setTitle(updatePostRequest.getTitle());
        post.setContent(updatePostRequest.getContent());
        post.setAuthor(updatePostRequest.getAuthor());
        post.setStatus(Status.WAITING_FOR_APPROVEL);
    }


    private PostResponse mapToPostResponse(Post post) {
        log.info("Mapping post: {}", post);
        return PostResponse.builder()
                .id(post.getId()).title(post.getTitle()).content(post.getContent())
                .author(post.getAuthor()).timeOfCreation(post.getTimeOfCreation())
                .status(post.getStatus()).build();
    }

    private UUID generateId(){
        log.info("Generating random UUID");
        return UUID.randomUUID();
    }

    @Override
    public List<PostResponse> filterPosts(String content, String author, String fromDate, String toDate, String userRole) {
        checkUserRoleAllUsers(userRole);
        log.info("Filtering posts");
        List<Post> filteredPosts = postRepository.getPostsByStatus(Status.POSTED);

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

        if ((fromDate != null && !fromDate.isEmpty()) && (toDate != null && !toDate.isEmpty())) {
            try {
                LocalDateTime from = LocalDate.parse(fromDate).atStartOfDay();
                LocalDateTime to = LocalDate.parse(toDate).atStartOfDay();

                filteredPosts = filteredPosts.stream()
                        .filter(post -> post.getTimeOfCreation() != null &&
                                !post.getTimeOfCreation().isBefore(from) &&
                                !post.getTimeOfCreation().isAfter(to))
                        .collect(Collectors.toList());
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format. Please use the format 'yyyy-MM-dd'.", e);
            }
        }

        return filteredPosts.stream()
                .map(this::mapToPostResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PostResponse addPostAsConcept(CreatePostRequest createPostRequest, String userRole) {
        checkUserRoleHoofdAndRedac(userRole);
        log.info("Adding new post as a concept: {}", createPostRequest.getTitle());
        Post post = Post.builder()
                .id(generateId())
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
        log.info("Saving notifications");
        Notification notification = Notification.builder().id(generateId()).postId(request.getPostId()).status(request.getStatus()).build();
        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationsDTO> GetAllNotifications(String userRole) {
        checkUserRoleHoofdAndRedac(userRole);
        log.info("Getting all notifications");
        List<Notification> allNotifications = notificationRepository.findAll();

        List<NotificationsDTO> notificationsDTOList = allNotifications.stream()
                .map(notification -> {
                    Post post = postRepository.findById(notification.getPostId())
                            .orElseThrow(() -> new RuntimeException("Post not found for id: " + notification.getPostId()));

                    NotificationsDTO dto = NotificationsDTO.builder()
                            .id(notification.getId())
                            .postTitle(post.getTitle())
                            .rejectionReason(post.getRejectionReason())
                            .postId(notification.getPostId())
                            .status(notification.getStatus())
                            .build();

                    return dto;
                })
                .collect(Collectors.toList());

        return notificationsDTOList;
    }

    @Override
    public List<PostResponse> getAllConceptPosts(String userRole) {
        checkUserRoleHoofdAndRedac(userRole);
        log.info("Getting all concept posts");
        return postRepository.getPostsByStatus(Status.CONCEPT).stream()
                .map(this::mapToPostResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PostResponse getConceptPostById(UUID id, String userRole) {
        checkUserRoleHoofdAndRedac(userRole);
        log.info("Getting concept post by id: {}", id);
        Post specificPost = postRepository.getReferenceById(id);

        if (specificPost.getStatus() != Status.CONCEPT && specificPost.getStatus() != Status.REJECTED) {
            throw new PostException("Post with id " + id + " is not a concept.");
        }
        return mapToPostResponse(specificPost);
    }

    @Override
    @Transactional
    public PostResponse makeConceptPosted(UpdatePostRequest updatePostRequest, UUID id, String userRole) {
        checkUserRoleHoofdAndRedac(userRole);
        log.info("Making concept posted: {}", id);
        validateUpdatePostRequest(updatePostRequest);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostException("Post with id " + id + " not found"));

        validatePostStatus(post);

        updatePostFieldsAndSendForReview(post, updatePostRequest);

        ReviewPostRequest reviewPostRequest = ReviewPostRequest.builder()
                .id(post.getId()).title(post.getTitle()).content(post.getContent())
                .author(post.getAuthor()).timeOfCreation(post.getTimeOfCreation()).status(post.getStatus())
                .rejectionReason(post.getRejectionReason()).build();

        rabbitTemplate.convertAndSend("NieuweTestQue", reviewPostRequest);
        rabbitTemplate.convertAndSend("NieuweTestQue", reviewPostRequest);

        postRepository.save(post);
        return mapToPostResponse(post);
    }


    @RabbitListener(queues = "getApprovalAnswer")
    public void receiveFromGetApprovalAnswerQueue(ReviewPostDTO post) {
        log.info("Getting approved posts as answer: {}", post);
        Post reviewedPost = Post.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getAuthor())
                .status(post.getStatus())
                .timeOfCreation(post.getTimeOfCreation())
                .rejectionReason(post.getRejectionReason())
                .build();

        postRepository.save(reviewedPost);
    }

    public void validateUpdatePostRequest(UpdatePostRequest updatePostRequest){
        if (updatePostRequest == null ||
                updatePostRequest.getTitle() == null ||
                updatePostRequest.getContent() == null ||
                updatePostRequest.getAuthor() == null) {
            throw new PostException("Invalid update request: title, content and author must not be null.");
        }
    }
}
