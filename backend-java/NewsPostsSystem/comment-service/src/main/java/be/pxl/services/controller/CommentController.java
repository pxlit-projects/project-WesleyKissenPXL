package be.pxl.services.controller;

import be.pxl.services.controller.DTO.CommentResponseDTO;
import be.pxl.services.controller.request.CommentRequest;
import be.pxl.services.controller.request.UpdateCommentRequest;
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


    @PostMapping("/{postId}/addComment")
    public ResponseEntity<CommentResponseDTO> addComment(@RequestHeader("Role") String userRole, @PathVariable UUID postId, @RequestBody CommentRequest comment) {
        CommentResponseDTO responseDTO = commentService.addComment(comment, postId, userRole);
        return ResponseEntity.ok(responseDTO);
    }


    @GetMapping("/{postId}/getComments")
    public ResponseEntity<List<CommentResponseDTO>> getComments(@RequestHeader("Role") String userRole, @PathVariable UUID postId) {
        return new ResponseEntity<>(commentService.getAllCommentsFrom(postId, userRole), HttpStatus.OK);
    }

    @GetMapping("/getAllComments")
    public ResponseEntity<List<CommentResponseDTO>> getAllComments(@RequestHeader("Role") String userRole) {
        return new ResponseEntity<>(commentService.getAllComments(userRole), HttpStatus.OK);
    }


    @PutMapping("/{id}/change")
    public ResponseEntity<CommentResponseDTO> changeComment(@RequestHeader("Role") String userRole, @PathVariable UUID id, @RequestBody UpdateCommentRequest updatededComment) {
        return ResponseEntity.ok(commentService.changeComment(id, updatededComment, userRole));
    }

    @DeleteMapping("/{id}/deleteComment")
    public ResponseEntity<String> deleteComment(@RequestHeader("Role") String userRole,@PathVariable UUID id) {
        commentService.deleteComment(id, userRole);
        return ResponseEntity.ok("Comment deleted successfully.");
    }
}
