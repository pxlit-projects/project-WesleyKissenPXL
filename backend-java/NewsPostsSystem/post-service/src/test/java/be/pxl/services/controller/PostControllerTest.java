package be.pxl.services.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import be.pxl.services.controller.dto.NotificationsDTO;
import be.pxl.services.controller.dto.PostResponse;
import be.pxl.services.controller.dto.PostStatusChangedRequestDTO;
import be.pxl.services.controller.request.CreatePostRequest;
import be.pxl.services.controller.request.UpdatePostRequest;
import be.pxl.services.domain.Status;
import be.pxl.services.services.IPostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

class PostControllerTest {

    @Mock
    private IPostService postService;

    @InjectMocks
    private PostController postController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddPost() {
        UUID postId = UUID.randomUUID();
        String userRole = "ROLE_ADMIN";
        CreatePostRequest postRequest = new CreatePostRequest("Title", "Content", "Author");
        PostResponse postResponse = new PostResponse(postId, "Title", "Content", "Author", LocalDateTime.of(2025, 1, 1, 12 , 0), Status.POSTED);

        when(postService.addPost(postRequest, userRole)).thenReturn(postResponse);

        ResponseEntity<PostResponse> response = postController.add(userRole, postRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(postResponse, response.getBody());
        verify(postService, times(1)).addPost(postRequest, userRole);
    }

    @Test
    void testAddPostAsConcept() {
        UUID postId = UUID.randomUUID();
        String userRole = "ROLE_ADMIN";
        CreatePostRequest postRequest = new CreatePostRequest("Title", "Content", "Author");
        PostResponse postResponse = new PostResponse(postId, "Title", "Content", "Author", LocalDateTime.of(2025, 1, 1, 12 , 0), Status.CONCEPT);

        when(postService.addPostAsConcept(postRequest, userRole)).thenReturn(postResponse);

        ResponseEntity<PostResponse> response = postController.addAsConcept(userRole, postRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(postResponse, response.getBody());
        verify(postService, times(1)).addPostAsConcept(postRequest, userRole);
    }

    @Test
    void testGetAllConcepts() {
        String userRole = "ROLE_ADMIN";
        List<PostResponse> postResponses = List.of(new PostResponse(UUID.randomUUID(), "Title", "Content", "Author", LocalDateTime.of(2025, 1, 1, 12 , 0), Status.CONCEPT));

        when(postService.getAllConceptPosts(userRole)).thenReturn(postResponses);

        ResponseEntity<List<PostResponse>> response = postController.getAllConcepts(userRole);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(postResponses, response.getBody());
        verify(postService, times(1)).getAllConceptPosts(userRole);
    }

    @Test
    void testGetConceptPostById() {
        UUID postId = UUID.randomUUID();
        String userRole = "ROLE_ADMIN";
        PostResponse postResponse = new PostResponse(postId, "Title", "Content", "Author", LocalDateTime.of(2025, 1, 1, 12 , 0), Status.CONCEPT);

        when(postService.getConceptPostById(postId, userRole)).thenReturn(postResponse);

        ResponseEntity<PostResponse> response = postController.getConceptPostById(userRole, postId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(postResponse, response.getBody());
        verify(postService, times(1)).getConceptPostById(postId, userRole);
    }

    @Test
    void testEditPost() {
        UUID postId = UUID.randomUUID();
        String userRole = "ROLE_ADMIN";
        UpdatePostRequest updatePostRequest = new UpdatePostRequest("", "Updated Content", "");
        PostResponse postResponse = new PostResponse(postId, "Title", "Updated Content", "Author", LocalDateTime.of(2025, 1, 1, 12 , 0), Status.POSTED);

        when(postService.changePost(updatePostRequest, postId, userRole)).thenReturn(postResponse);

        ResponseEntity<PostResponse> response = postController.editPost(userRole, postId, updatePostRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(postResponse, response.getBody());
        verify(postService, times(1)).changePost(updatePostRequest, postId, userRole);
    }

    @Test
    void testMakeConceptPosted() {
        UUID postId = UUID.randomUUID();
        String userRole = "ROLE_ADMIN";
        UpdatePostRequest updatePostRequest = new UpdatePostRequest("", "Updated Content", "");
        PostResponse postResponse = new PostResponse(postId, "Title", "Posted Content", "Author", LocalDateTime.of(2025, 1, 1, 12 , 0), Status.POSTED);

        when(postService.makeConceptPosted(updatePostRequest, postId, userRole)).thenReturn(postResponse);

        ResponseEntity<PostResponse> response = postController.makeConceptPosted(userRole, postId, updatePostRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(postResponse, response.getBody());
        verify(postService, times(1)).makeConceptPosted(updatePostRequest, postId, userRole);
    }

    @Test
    void testGetAllPosted() {
        String userRole = "ROLE_ADMIN";
        List<PostResponse> postResponses = List.of(new PostResponse(UUID.randomUUID(), "Title", "Content", "Author", LocalDateTime.of(2025, 1, 1, 12 , 0), Status.POSTED));

        when(postService.getAllPostedPosts(userRole)).thenReturn(postResponses);

        ResponseEntity<List<PostResponse>> response = postController.getAllPosted(userRole);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(postResponses, response.getBody());
        verify(postService, times(1)).getAllPostedPosts(userRole);
    }

    @Test
    void testFilterPosts() {
        String userRole = "ROLE_ADMIN";
        List<PostResponse> postResponses = List.of(new PostResponse(UUID.randomUUID(), "Title", "Content", "Author", LocalDateTime.of(2025, 1, 1, 12 , 0), Status.POSTED));

        when(postService.filterPosts("Content", "Author", "2025-01-01", "2025-01-31", userRole)).thenReturn(postResponses);

        ResponseEntity<List<PostResponse>> response = postController.filterPosts(userRole, "Content", "Author", "2025-01-01", "2025-01-31");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(postResponses, response.getBody());
        verify(postService, times(1)).filterPosts("Content", "Author", "2025-01-01", "2025-01-31", userRole);
    }

    @Test
    void testGetAllNotifications() {
        String userRole = "ROLE_ADMIN";
        List<NotificationsDTO> notifications = List.of(new NotificationsDTO());

        when(postService.GetAllNotifications(userRole)).thenReturn(notifications);

        ResponseEntity<List<NotificationsDTO>> response = postController.handlePostStatusChange(userRole);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(notifications, response.getBody());
        verify(postService, times(1)).GetAllNotifications(userRole);
    }
}