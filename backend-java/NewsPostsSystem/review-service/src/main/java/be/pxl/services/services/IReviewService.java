package be.pxl.services.services;

import be.pxl.services.controller.dto.ReviewPostDTO;
import be.pxl.services.controller.request.RejectionMessageRequest;

import java.util.List;
import java.util.UUID;

public interface IReviewService {
    List<ReviewPostDTO> getAllReviewablePosts();

    ReviewPostDTO rejectPost(UUID postId, RejectionMessageRequest rejectionMessage);
}
