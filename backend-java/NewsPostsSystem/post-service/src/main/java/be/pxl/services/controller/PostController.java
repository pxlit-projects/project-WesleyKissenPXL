package be.pxl.services.controller;

import be.pxl.services.controller.dto.PostResponse;
import be.pxl.services.controller.dto.PostStatusChangedRequestDTO;
import be.pxl.services.controller.request.CreatePostRequest;
import be.pxl.services.controller.request.UpdatePostRequest;
import be.pxl.services.domain.Post;
import be.pxl.services.services.IPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.commons.security.AccessTokenContextRelay;
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
    public ResponseEntity<PostResponse> add(@RequestBody CreatePostRequest postRequest) {
        PostResponse postResponse = postService.addPost(postRequest);
        return ResponseEntity.ok(postResponse);
    }

    @PostMapping("/addAsConcept")
    public ResponseEntity<PostResponse> addAsConcept(@RequestBody CreatePostRequest postRequest) {
        PostResponse postResponse = postService.addPostAsConcept(postRequest);
        return ResponseEntity.ok(postResponse);
    }

    @PutMapping("/change/{id}")
    public ResponseEntity<PostResponse> editPost(@PathVariable UUID id, @Valid @RequestBody UpdatePostRequest updatePostRequest) {
        PostResponse postResponse = postService.changePost(updatePostRequest, id);
        return ResponseEntity.ok(postResponse);
    }

    @GetMapping("/getAllPosted")
    public ResponseEntity<List<PostResponse>> getAllPosted() {
        return new ResponseEntity<>(postService.getAllPostedPosts(), HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<PostResponse>> filterPosts(
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {
        List<PostResponse> filteredPosts = postService.filterPosts(content, author, fromDate, toDate);
        return ResponseEntity.ok(filteredPosts);
    }

    @PostMapping("/notifications")
    public void handlePostStatusChange(@RequestBody PostStatusChangedRequestDTO request) {
        postService.saveNotifications(request);
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<PostStatusChangedRequestDTO>> handlePostStatusChange() {
        return new ResponseEntity<>(postService.GetAllNotifications(), HttpStatus.OK);
    }

}
