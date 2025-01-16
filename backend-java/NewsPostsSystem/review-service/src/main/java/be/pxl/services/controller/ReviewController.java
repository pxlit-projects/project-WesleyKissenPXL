package be.pxl.services.controller;

import be.pxl.services.controller.dto.ReviewPostDTO;
import be.pxl.services.controller.request.RejectionMessageRequest;
import be.pxl.services.services.IReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final IReviewService reviewService;

    @GetMapping("/getAllReviewablePosts")
    public ResponseEntity<List<ReviewPostDTO>> getAllReviewablePosts(@RequestHeader("Role") String userRole) {
        return new ResponseEntity<>(reviewService.getAllReviewablePosts(userRole), HttpStatus.OK);
    }


    @PutMapping("/{postId}/reject")
    public ResponseEntity<ReviewPostDTO> reject(
            @RequestHeader("Role") String userRole,
            @PathVariable UUID postId,
            @RequestBody RejectionMessageRequest rejectionMessage) {

        ReviewPostDTO reviewPost = reviewService.rejectPost(postId, rejectionMessage, userRole);

        return ResponseEntity.ok(reviewPost);
    }

    @PutMapping("/{postId}/publishReviewPost")
    public ResponseEntity<ReviewPostDTO> publish(
            @RequestHeader("Role") String userRole,
            @PathVariable UUID postId) {

        ReviewPostDTO reviewPost = reviewService.publishPost(postId, userRole);

        return ResponseEntity.ok(reviewPost);
    }
}
