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

/**
 * En REST kontroller som hanterar alla anrop som rör kommentarer,
 * som exempelvis att skapa, hämta, uppdatera eller ta bort kommentarer.
 */
@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Skapar och sparar en ny kommentar i databasen.
     *
     * @param dto            - Information om kommentaren som ska skapas.
     * @param authentication - inloggad användare
     * @return - den skapade kommentaren.
     */
    @PostMapping
    public ResponseEntity<CommentResponseDTO> createComment(
            @RequestBody CommentRequestDTO dto, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(dto, authentication.getName()));
    }

    /**
     * Hämtar alla kommentarer som hör till ett specifikt inlägg.
     *
     * @param postId         - inläggets ID
     * @param pageable       - information om sida, storlek och sortering.
     * @param authentication - inloggad användare
     * @return - en sida med kommentarer
     */
    @GetMapping("/post/{postId}")
    public Page<CommentResponseDTO> getComments(
            @PathVariable Long postId,
            @ParameterObject @PageableDefault(
                    size = 5, sort = "createdAt", direction = Sort.Direction.ASC)
            Pageable pageable, Authentication authentication) {
        return commentService.getCommentsForPost(postId, pageable, authentication.getName());
    }

    /**
     * Uppdaterar innehållet i en befintlig kommentar med specifikt id.
     *
     * @param id             - id på kommentaren som ska uppdateras
     * @param dto            - nytt innehåll
     * @param authentication - inloggad användare
     * @return - den uppdaterade kommentaren
     */
    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable Long id,
            @RequestBody CommentRequestDTO dto,
            Authentication authentication) {
        return ResponseEntity.ok(commentService.updateComment(id, dto.content(), authentication.getName()));
    }

    /**
     * Tar bort en kommentar permanent.
     *
     * @param id             - id på kommentaren som ska tas bort
     * @param authentication - inloggad användare
     * @return - tomt svar med status kod 204
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, Authentication authentication) {
        commentService.deleteComment(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }


    /**
     * Metod som gör att man kan gilla eller ångra en gilla-markering på en kommentar.
     * Om användaren redan har gillat kommentaren tas markeringen bort.
     *
     * @param id             - kommentars id
     * @param authentication - inloggad användare
     * @return - tomt svar med status kod 200
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeComment(@PathVariable Long id, Authentication authentication) {
        commentService.toggleLike(id, authentication.getName());
        return ResponseEntity.ok().build();
    }
}
