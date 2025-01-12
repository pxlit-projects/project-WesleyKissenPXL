package be.pxl.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import be.pxl.services.controller.dto.PostResponse;
import be.pxl.services.controller.dto.PostStatusChangedRequestDTO;
import be.pxl.services.controller.dto.ReviewPostDTO;
import be.pxl.services.controller.request.CreatePostRequest;
import be.pxl.services.controller.request.UpdatePostRequest;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.Status;
import be.pxl.services.exception.PostException;
import be.pxl.services.repository.PostRepository;
import be.pxl.services.services.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddPost() {
        CreatePostRequest createPostRequest = new CreatePostRequest("Title", "Content", "Author");
        Post post = Post.builder()
                .id(UUID.randomUUID())
                .title("Title")
                .content("Content")
                .author("Author")
                .timeOfCreation(null)
                .status(Status.WAITING_FOR_APPROVEL)
                .build();

        when(postRepository.save(any(Post.class))).thenReturn(post);


        PostResponse postResponse = postService.addPost(createPostRequest);

        assertNotNull(postResponse);
        assertEquals("Title", postResponse.getTitle());
        assertEquals("Content", postResponse.getContent());
        verify(postRepository, times(1)).save(any(Post.class)); // Ensure save was called once

    }

    @Test
    void testChangePost() {
        UUID postId = UUID.randomUUID();
        UpdatePostRequest updatePostRequest = new UpdatePostRequest("New Title", "New Content");
        Post post = Post.builder()
                .id(postId)
                .title("Old Title")
                .content("Old Content")
                .author("Author")
                .timeOfCreation(null)
                .status(Status.CONCEPT)
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        PostResponse updatedPostResponse = postService.changePost(updatePostRequest, postId);

        assertNotNull(updatedPostResponse);
        assertEquals("New Title", updatedPostResponse.getTitle());
        assertEquals("New Content", updatedPostResponse.getContent());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void testChangePostThrowsExceptionWhenPostNotFound() {
        UUID postId = UUID.randomUUID();
        UpdatePostRequest updatePostRequest = new UpdatePostRequest("New Title", "New Content");

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        PostException thrown = assertThrows(PostException.class, () -> postService.changePost(updatePostRequest, postId));
        assertEquals("Post with id " + postId + " not found", thrown.getMessage());
    }

    @Test
    void testGetAllPostedPosts() {
        Post post = Post.builder()
                .id(UUID.randomUUID())
                .title("Title")
                .content("Content")
                .author("Author")
                .timeOfCreation(null)
                .status(Status.POSTED)
                .build();

        when(postRepository.getPostsByStatus(Status.POSTED)).thenReturn(List.of(post));

        List<PostResponse> posts = postService.getAllPostedPosts();

        assertNotNull(posts);
        assertEquals(1, posts.size());
        assertEquals("Title", posts.get(0).getTitle());
    }

    @Test
    void testFilterPosts() {
        Post post1 = Post.builder()
                .id(UUID.randomUUID())
                .title("Title 1")
                .content("Content 1")
                .author("Author 1")
                .timeOfCreation(null)
                .status(Status.POSTED)
                .build();

        Post post2 = Post.builder()
                .id(UUID.randomUUID())
                .title("Title 2")
                .content("Content 2")
                .author("Author 2")
                .timeOfCreation(null)
                .status(Status.POSTED)
                .build();

        when(postRepository.findAll()).thenReturn(List.of(post1, post2));

        List<PostResponse> filteredPosts = postService.filterPosts("Content 1", "Author 1", null, null);

        assertNotNull(filteredPosts);
        assertEquals(1, filteredPosts.size());
        assertEquals("Content 1", filteredPosts.get(0).getContent());
    }

    @Test
    void testSaveNotifications() {
        PostStatusChangedRequestDTO request = new PostStatusChangedRequestDTO(UUID.randomUUID(), Status.POSTED);
        postService.saveNotifications(request);

        List<PostStatusChangedRequestDTO> notifications = postService.GetAllNotifications();
        assertNotNull(notifications);
        assertEquals(1, notifications.size());
        assertEquals(request, notifications.get(0));
    }

    @Test
    void testReceiveFromGetApprovalAnswerQueue() {
        ReviewPostDTO reviewPostDTO = new ReviewPostDTO(UUID.randomUUID(), "Title", "Content", "Author", LocalDateTime.now(),Status.POSTED, null);
        Post post = new Post(UUID.randomUUID(), "Title", "Content", "Author", null, Status.POSTED, null);

        when(postRepository.save(any(Post.class))).thenReturn(post);

        postService.receiveFromGetApprovalAnswerQueue(reviewPostDTO);

        verify(postRepository, times(1)).save(any(Post.class));
    }
}
