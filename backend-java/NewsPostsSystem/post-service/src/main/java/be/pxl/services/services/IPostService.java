package be.pxl.services.services;

import be.pxl.services.controller.dto.NotificationsDTO;
import be.pxl.services.controller.dto.PostResponse;
import be.pxl.services.controller.dto.PostStatusChangedRequestDTO;
import be.pxl.services.controller.request.CreatePostRequest;
import be.pxl.services.controller.request.UpdatePostRequest;
import be.pxl.services.domain.Notification;

import java.util.List;
import java.util.UUID;

public interface IPostService {
    PostResponse addPost(CreatePostRequest createPostRequest, String userRole);

    PostResponse changePost(UpdatePostRequest updatePostRequest, UUID id, String userRole);

    List<PostResponse> getAllPostedPosts(String userRole);

    List<PostResponse> filterPosts(String content, String author, String fromDate, String toDate, String userRole);

    PostResponse addPostAsConcept(CreatePostRequest postRequest, String userRole);

    void saveNotifications(PostStatusChangedRequestDTO request);

    List<NotificationsDTO> GetAllNotifications(String userRole);

    List<PostResponse> getAllConceptPosts(String userRole);

    PostResponse getConceptPostById(UUID id, String userRole);

    PostResponse makeConceptPosted(UpdatePostRequest updatePostRequest, UUID id, String userRole);

}

