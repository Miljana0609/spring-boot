package se.jensen.alexandra.springboot2.dto;

/**
 * DTO som används för att ta emot data från frontend när en användare skapar en ny kommentar.
 *
 * @param postId          - inläggets id
 * @param content         - textinnehållet i kommentaren
 * @param parentCommentId - id på kommentaren som besvaras (null om det är huvudkommentar)
 */
public record CommentRequestDTO(
        Long postId,
        String content,
        Long parentCommentId
) {
}
