package se.jensen.alexandra.springboot2.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import se.jensen.alexandra.springboot2.dto.CommentRequestDTO;
import se.jensen.alexandra.springboot2.dto.CommentResponseDTO;
import se.jensen.alexandra.springboot2.service.CommentService;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponseDTO> createComment(
            @RequestBody CommentRequestDTO dto, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(dto, authentication.getName()));
    }

    @GetMapping("/post/{postId}")
    public Page<CommentResponseDTO> getComments(
            @PathVariable Long postId,
            @ParameterObject @PageableDefault(
                    size = 5, sort = "createdAt", direction = Sort.Direction.ASC)
            Pageable pageable, Authentication authentication) {
        return commentService.getCommentsForPost(postId, pageable, authentication.getName());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable Long id,
            @RequestBody CommentRequestDTO dto,
            Authentication authentication) {
        return ResponseEntity.ok(commentService.updateComment(id, dto.content(), authentication.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, Authentication authentication) {
        commentService.deleteComment(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeComment(@PathVariable Long id, Authentication authentication) {
        commentService.toggleLike(id, authentication.getName());
        return ResponseEntity.ok().build();
    }
}
