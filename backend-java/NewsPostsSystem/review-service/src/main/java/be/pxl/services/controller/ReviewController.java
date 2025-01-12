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
    public ResponseEntity<List<ReviewPostDTO>> getAllReviewablePosts() {
        return new ResponseEntity<>(reviewService.getAllReviewablePosts(), HttpStatus.OK);
    }


    @PostMapping("/{postId}/reject")
    public ResponseEntity<ReviewPostDTO> reject(
            @PathVariable UUID postId,
            @RequestBody RejectionMessageRequest rejectionMessage) {

        ReviewPostDTO reviewPost = reviewService.rejectPost(postId, rejectionMessage);

        return ResponseEntity.ok(reviewPost);
    }

}
