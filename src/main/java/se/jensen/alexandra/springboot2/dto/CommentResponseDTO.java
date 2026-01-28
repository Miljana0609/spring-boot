package se.jensen.alexandra.springboot2.dto;

import java.time.LocalDateTime;

/**
 * DTO som skickas tillbaka till frontend för att visas upp.
 * Innehåller all information som behövs för att presentera en kommentar.
 *
 * @param id              - id på kommentaren
 * @param content         - kommentarens text
 * @param createdAt       - tidpunkt då kommentaren skapades
 * @param userId          - användarens id som skrev kommentaren
 * @param username        - användarnamn
 * @param displayName     - visningsnamn
 * @param likeCount       - antal gilla-markeringar
 * @param likedByMe       - true om den inloggade användaren har gillat kommentaren
 * @param parentCommentId - id på kommentaren som denna är ett svar på (null om ingen)
 */
public record CommentResponseDTO(
        Long id,
        String content,
        LocalDateTime createdAt,
        Long userId,
        String username,
        String displayName,
        int likeCount,
        boolean likedByMe,
        Long parentCommentId

) {
}
