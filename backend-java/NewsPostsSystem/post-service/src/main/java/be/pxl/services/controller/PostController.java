package be.pxl.services.controller;

import be.pxl.services.controller.dto.NotificationsDTO;
import be.pxl.services.controller.dto.PostResponse;
import be.pxl.services.controller.dto.PostStatusChangedRequestDTO;
import be.pxl.services.controller.request.CreatePostRequest;
import be.pxl.services.controller.request.UpdatePostRequest;
import be.pxl.services.services.IPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final IPostService postService;

    @PostMapping("/add")
    public ResponseEntity<PostResponse> add(@RequestHeader("Role") String userRole, @RequestBody CreatePostRequest postRequest) {
        PostResponse postResponse = postService.addPost(postRequest, userRole);
        return ResponseEntity.ok(postResponse);
    }

    @PostMapping("/addAsConcept")
    public ResponseEntity<PostResponse> addAsConcept(@RequestHeader("Role") String userRole, @RequestBody CreatePostRequest postRequest) {
        PostResponse postResponse = postService.addPostAsConcept(postRequest, userRole);
        return ResponseEntity.ok(postResponse);
    }

    @GetMapping("/getAllConcepts")
    public ResponseEntity<List<PostResponse>> getAllConcepts(@RequestHeader("Role") String userRole) {
        List<PostResponse> postResponse = postService.getAllConceptPosts(userRole);
        return ResponseEntity.ok(postResponse);
    }

    @GetMapping("/getConceptPost/{id}")
    public ResponseEntity<PostResponse> getConceptPostById(@RequestHeader("Role") String userRole, @PathVariable UUID id) {
        PostResponse postResponse = postService.getConceptPostById(id, userRole);
        return ResponseEntity.ok(postResponse);
    }

    @PutMapping("/change/{id}")
    public ResponseEntity<PostResponse> editPost(@RequestHeader("Role") String userRole, @PathVariable UUID id, @Valid @RequestBody UpdatePostRequest updatePostRequest) {
        PostResponse postResponse = postService.changePost(updatePostRequest, id, userRole);
        return ResponseEntity.ok(postResponse);
    }

    @PutMapping("/conceptPosted/{id}")
    public ResponseEntity<PostResponse> makeConceptPosted(@RequestHeader("Role") String userRole,@PathVariable UUID id, @Valid @RequestBody UpdatePostRequest updatePostRequest) {
        PostResponse postResponse = postService.makeConceptPosted(updatePostRequest, id, userRole);
        return ResponseEntity.ok(postResponse);
    }

    @GetMapping("/getAllPosted")
    public ResponseEntity<List<PostResponse>> getAllPosted(@RequestHeader("Role") String userRole) {
        return new ResponseEntity<>(postService.getAllPostedPosts(userRole), HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<PostResponse>> filterPosts(
            @RequestHeader("Role") String userRole,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {
        List<PostResponse> filteredPosts = postService.filterPosts(content, author, fromDate, toDate, userRole);
        return ResponseEntity.ok(filteredPosts);
    }

    @PostMapping("/notifications")
    public void handlePostStatusChange(@RequestBody PostStatusChangedRequestDTO request) {
        postService.saveNotifications(request);
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationsDTO>> handlePostStatusChange(@RequestHeader("Role") String userRole) {
        return ResponseEntity.ok(postService.GetAllNotifications(userRole));
    }

}
