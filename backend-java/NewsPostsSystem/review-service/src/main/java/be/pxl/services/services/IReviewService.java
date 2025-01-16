package be.pxl.services.services;

import be.pxl.services.controller.dto.ReviewPostDTO;
import be.pxl.services.controller.request.RejectionMessageRequest;

import java.util.List;
import java.util.UUID;

public interface IReviewService {
    List<ReviewPostDTO> getAllReviewablePosts(String userRole);

    ReviewPostDTO rejectPost(UUID postId, RejectionMessageRequest rejectionMessage, String userRole);
    void receiveFromGetApprovalQueue(ReviewPostDTO post);

    ReviewPostDTO publishPost(UUID postId, String userRole);
}
