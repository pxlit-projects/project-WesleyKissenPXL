package be.pxl.services.services;

import be.pxl.services.controller.dto.PostResponse;
import be.pxl.services.controller.dto.PostStatusChangedRequestDTO;
import be.pxl.services.controller.request.CreatePostRequest;
import be.pxl.services.controller.request.UpdatePostRequest;

import java.util.List;
import java.util.UUID;

public interface IPostService {
    PostResponse addPost(CreatePostRequest createPostRequest);

    PostResponse changePost(UpdatePostRequest updatePostRequest, UUID id);

    List<PostResponse> getAllPostedPosts();

    List<PostResponse> filterPosts(String content, String author, String fromDate, String toDate);

    PostResponse addPostAsConcept(CreatePostRequest postRequest);

    void saveNotifications(PostStatusChangedRequestDTO request);

    List<PostStatusChangedRequestDTO> GetAllNotifications();
}

