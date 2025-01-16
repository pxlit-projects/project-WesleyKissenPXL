package be.pxl.services.services;

import be.pxl.services.controller.dto.NotificationsDTO;
import be.pxl.services.controller.dto.PostResponse;
import be.pxl.services.controller.dto.PostStatusChangedRequestDTO;
import be.pxl.services.controller.dto.ReviewPostDTO;
import be.pxl.services.controller.request.CreatePostRequest;
import be.pxl.services.controller.request.UpdatePostRequest;
import be.pxl.services.domain.Notification;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.Status;
import be.pxl.services.exception.PostException;
import be.pxl.services.repository.NotificationRepository;
import be.pxl.services.repository.PostRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PostService postService;

    private UUID postId;
    private CreatePostRequest createPostRequest;
    private UpdatePostRequest updatePostRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        postId = UUID.randomUUID();
        createPostRequest = new CreatePostRequest("Post Title", "Post Content", "Author");
        updatePostRequest = new UpdatePostRequest("Updated Title", "Updated Content", "Author");
    }

    @Test
    void testCheckUserRoleHoofdAndRedac_ValidRole() {
        postService.checkUserRoleHoofdAndRedac("hoofdredacteur");
        postService.checkUserRoleHoofdAndRedac("redacteur");
    }

    @Test
    void testCheckUserRoleHoofdAndRedac_InvalidRole() {
        PostException exception = assertThrows(PostException.class, () -> postService.checkUserRoleHoofdAndRedac("gebruiker"));
        assertEquals("Invalid user role", exception.getMessage());
    }

    @Test
    void testCheckUserRoleAllUsers_ValidRole() {
        postService.checkUserRoleAllUsers("hoofdredacteur");
        postService.checkUserRoleAllUsers("redacteur");
        postService.checkUserRoleAllUsers("gebruiker");
    }

    @Test
    void testCheckUserRoleAllUsers_InvalidRole() {
        PostException exception = assertThrows(PostException.class, () -> postService.checkUserRoleAllUsers("admin"));
        assertEquals("Invalid user role", exception.getMessage());
    }

    @Test
    @Transactional
    void testAddPost() {
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), any(Object.class));
        when(postRepository.save(any(Post.class))).thenReturn(new Post());

        PostResponse response = postService.addPost(createPostRequest, "hoofdredacteur");

        assertNotNull(response);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void testChangePost_ValidRequest() {
        Post post = new Post(postId, "Title", "Content", "Author", LocalDateTime.now(), Status.CONCEPT, "");
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        PostResponse response = postService.changePost(updatePostRequest, postId, "hoofdredacteur");

        assertNotNull(response);
        assertEquals("Updated Title", response.getTitle());
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void testChangePost_PostNotFound() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        PostException exception = assertThrows(PostException.class, () -> postService.changePost(updatePostRequest, postId, "hoofdredacteur"));
        assertEquals("Post with id " + postId + " not found", exception.getMessage());
    }

    @Test
    void testGetAllPostedPosts() {
        when(postRepository.getPostsByStatus(Status.POSTED)).thenReturn(List.of(new Post()));

        List<PostResponse> responses = postService.getAllPostedPosts("gebruiker");

        assertNotNull(responses);
        assertFalse(responses.isEmpty());
        verify(postRepository).getPostsByStatus(Status.POSTED);
    }



    @Test
    void testAddPostAsConcept() {
        when(postRepository.save(any(Post.class))).thenReturn(new Post());

        PostResponse response = postService.addPostAsConcept(createPostRequest, "hoofdredacteur");

        assertNotNull(response);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void testSaveNotifications() {
        PostStatusChangedRequestDTO request = new PostStatusChangedRequestDTO(postId, Status.POSTED);
        postService.saveNotifications(request);

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void testGetAllNotifications() {
        Notification notification = new Notification( UUID.randomUUID() ,postId, Status.POSTED);
        when(notificationRepository.findAll()).thenReturn(List.of(notification));
        when(postRepository.findById(postId)).thenReturn(Optional.of(new Post()));

        List<NotificationsDTO> notifications = postService.GetAllNotifications("hoofdredacteur");

        assertNotNull(notifications);
        assertFalse(notifications.isEmpty());
        verify(notificationRepository).findAll();
    }

    @Test
    void testMakeConceptPosted() {
        Post post = new Post(postId, "Title", "Content", "Author", LocalDateTime.now(), Status.CONCEPT, "");
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), any(Object.class));


        PostResponse response = postService.makeConceptPosted(updatePostRequest, postId, "hoofdredacteur");

        assertNotNull(response);
        verify(postRepository).save(any(Post.class));
        verify(rabbitTemplate, times(2)).convertAndSend(anyString(), any(Object.class));
    }

    @Test
    void testGetConceptPostById() {
        Post post = new Post(postId, "Title", "Content", "Author", LocalDateTime.now(), Status.CONCEPT, "");
        when(postRepository.getReferenceById(postId)).thenReturn(post);

        PostResponse response = postService.getConceptPostById(postId, "hoofdredacteur");

        assertNotNull(response);
        assertEquals("Title", response.getTitle());
    }

    @Test
    void testGetConceptPostById_NotConcept() {
        Post post = new Post(postId, "Title", "Content", "Author", LocalDateTime.now(), Status.POSTED, "");
        when(postRepository.getReferenceById(postId)).thenReturn(post);

        PostException exception = assertThrows(PostException.class, () -> postService.getConceptPostById(postId, "hoofdredacteur"));
        assertEquals("Post with id " + postId + " is not a concept.", exception.getMessage());
    }

    @Test
    void testReceiveFromGetApprovalAnswerQueue() {
        ReviewPostDTO reviewPostDTO = new ReviewPostDTO(postId, "Title", "Content", "Author", LocalDateTime.now(), Status.POSTED, "No Rejection");
        postService.receiveFromGetApprovalAnswerQueue(reviewPostDTO);

        verify(postRepository).save(any(Post.class));
    }

    @Test
    void testValidateUpdatePostRequest_ValidRequest() {
        // Geval waar alle velden in de UpdatePostRequest geldig zijn
        UpdatePostRequest validRequest = new UpdatePostRequest("Valid Title", "Valid Content", "Valid Author");

        // Geen uitzondering wordt verwacht
        assertDoesNotThrow(() -> postService.validateUpdatePostRequest(validRequest));
    }

    @Test
    void testValidateUpdatePostRequest_NullTitle() {
        // Geval waarin de titel null is
        UpdatePostRequest invalidRequest = new UpdatePostRequest(null, "Valid Content", "Valid Author");

        // Controleer of de juiste uitzondering wordt gegooid
        PostException exception = assertThrows(PostException.class, () -> postService.validateUpdatePostRequest(invalidRequest));
        assertEquals("Invalid update request: title, content and author must not be null.", exception.getMessage());
    }

    @Test
    void testValidateUpdatePostRequest_NullContent() {
        // Geval waarin de content null is
        UpdatePostRequest invalidRequest = new UpdatePostRequest("Valid Title", null, "Valid Author");

        // Controleer of de juiste uitzondering wordt gegooid
        PostException exception = assertThrows(PostException.class, () -> postService.validateUpdatePostRequest(invalidRequest));
        assertEquals("Invalid update request: title, content and author must not be null.", exception.getMessage());
    }

    @Test
    void testValidateUpdatePostRequest_NullAuthor() {
        // Geval waarin de auteur null is
        UpdatePostRequest invalidRequest = new UpdatePostRequest("Valid Title", "Valid Content", null);

        // Controleer of de juiste uitzondering wordt gegooid
        PostException exception = assertThrows(PostException.class, () -> postService.validateUpdatePostRequest(invalidRequest));
        assertEquals("Invalid update request: title, content and author must not be null.", exception.getMessage());
    }

    @Test
    void testValidateUpdatePostRequest_AllNull() {
        // Geval waarin alle velden null zijn
        UpdatePostRequest invalidRequest = new UpdatePostRequest(null, null, null);

        // Controleer of de juiste uitzondering wordt gegooid
        PostException exception = assertThrows(PostException.class, () -> postService.validateUpdatePostRequest(invalidRequest));
        assertEquals("Invalid update request: title, content and author must not be null.", exception.getMessage());
    }

    @Test
    void testGetAllConceptPosts_ValidRole() {
        // Simuleer dat er conceptposts zijn in de repository
        when(postRepository.getPostsByStatus(Status.CONCEPT)).thenReturn(List.of(new Post()));

        // Voer de methode uit
        List<PostResponse> responses = postService.getAllConceptPosts("hoofdredacteur");

        // Controleer of de lijst van conceptposts niet leeg is
        assertNotNull(responses);
        assertFalse(responses.isEmpty());
        verify(postRepository).getPostsByStatus(Status.CONCEPT);
    }

    @Test
    void testGetAllConceptPosts_InvalidRole() {
        // Simuleer een ongeldige rol
        PostException exception = assertThrows(PostException.class, () -> postService.getAllConceptPosts("gebruiker"));
        assertEquals("Invalid user role", exception.getMessage());
    }

    @Test
    void testGetAllConceptPosts_EmptyList() {
        // Simuleer dat er geen conceptposts zijn
        when(postRepository.getPostsByStatus(Status.CONCEPT)).thenReturn(Collections.emptyList());

        // Voer de methode uit
        List<PostResponse> responses = postService.getAllConceptPosts("hoofdredacteur");

        // Controleer of de lijst leeg is
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(postRepository).getPostsByStatus(Status.CONCEPT);
    }

    @Test
    void testFilterPosts_ByContent() {
        // Arrange
        List<Post> posts = List.of(
                new Post(postId, "Title", "This is a post about content", "Author", LocalDateTime.now(), Status.POSTED, ""),
                new Post(UUID.randomUUID(), "Another Title", "Content not matching", "Another Author", LocalDateTime.now(), Status.POSTED, "")
        );
        when(postRepository.getPostsByStatus(Status.POSTED)).thenReturn(posts);

        // Act
        List<PostResponse> responses = postService.filterPosts("content", null, null, null, "gebruiker");

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertTrue(responses.get(0).getContent().contains("content"));
        verify(postRepository).getPostsByStatus(Status.POSTED);
    }

    @Test
    void testFilterPosts_ByAuthor() {
        // Arrange
        List<Post> posts = List.of(
                new Post(postId, "Title", "Some content", "Author", LocalDateTime.now(), Status.POSTED, ""),
                new Post(UUID.randomUUID(), "Another Title", "More content", "Different Author", LocalDateTime.now(), Status.POSTED, "")
        );
        when(postRepository.getPostsByStatus(Status.POSTED)).thenReturn(posts);

        // Act
        List<PostResponse> responses = postService.filterPosts(null, "Author", null, null, "gebruiker");

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Author", responses.get(0).getAuthor());
        verify(postRepository).getPostsByStatus(Status.POSTED);
    }

    @Test
    void testFilterPosts_ByContentAndAuthor() {
        // Arrange
        List<Post> posts = List.of(
                new Post(postId, "Title", "Content about post", "Author", LocalDateTime.now(), Status.POSTED, ""),
                new Post(UUID.randomUUID(), "Another Title", "Content not matching", "Author", LocalDateTime.now(), Status.POSTED, "")
        );
        when(postRepository.getPostsByStatus(Status.POSTED)).thenReturn(posts);

        // Act
        List<PostResponse> responses = postService.filterPosts("post", "Author", null, null, "gebruiker");

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertTrue(responses.get(0).getContent().contains("post"));
        assertEquals("Author", responses.get(0).getAuthor());
        verify(postRepository).getPostsByStatus(Status.POSTED);
    }

    @Test
    void testFilterPosts_InvalidDateFormat() {
        // Arrange
        when(postRepository.getPostsByStatus(Status.POSTED)).thenReturn(Collections.emptyList());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> postService.filterPosts(null, null, "invalid-date", "2025-01-01", "gebruiker"));
        assertEquals("Invalid date format. Please use the format 'yyyy-MM-dd'.", exception.getMessage());
    }

    @Test
    void testFilterPosts_EmptyResult() {
        // Arrange
        when(postRepository.getPostsByStatus(Status.POSTED)).thenReturn(Collections.emptyList());

        // Act
        List<PostResponse> responses = postService.filterPosts("Non matching content", "Non matching author", null, null, "gebruiker");

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(postRepository).getPostsByStatus(Status.POSTED);
    }

    @Test
    void testFilterPosts_NoFiltersApplied() {
        // Arrange
        List<Post> posts = List.of(
                new Post(postId, "Title", "Content", "Author", LocalDateTime.now(), Status.POSTED, ""),
                new Post(UUID.randomUUID(), "Another Title", "Another Content", "Another Author", LocalDateTime.now(), Status.POSTED, "")
        );
        when(postRepository.getPostsByStatus(Status.POSTED)).thenReturn(posts);

        // Act
        List<PostResponse> responses = postService.filterPosts(null, null, null, null, "gebruiker");

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(postRepository).getPostsByStatus(Status.POSTED);
    }
}
