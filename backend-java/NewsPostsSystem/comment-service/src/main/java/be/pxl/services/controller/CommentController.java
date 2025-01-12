package be.pxl.services.controller;

import be.pxl.services.controller.DTO.CommentResponseDTO;
import be.pxl.services.controller.request.CommentRequest;
import be.pxl.services.services.ICommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final ICommentService commentService;


    @PostMapping("{postId}/addComment")
    public ResponseEntity<CommentResponseDTO> addComment(@PathVariable UUID postId, @RequestBody CommentRequest comment) {
        CommentResponseDTO responseDTO = commentService.addComment(comment, postId);
        return ResponseEntity.ok(responseDTO);
    }


    @GetMapping("{postId}/getComments")
    public ResponseEntity<List<CommentResponseDTO>> getComments(@PathVariable UUID postId) {
        return new ResponseEntity<>(commentService.getAllCommentsFrom(postId), HttpStatus.OK);
    }
}
