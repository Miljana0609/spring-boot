package se.jensen.alexandra.springboot2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * En klass som används när man skickar tillbaka inlägg till frontend.
 *
 * @param id        - inläggets ID
 * @param text      - texten i inlägget
 * @param createdAt - Tidpunkt när inlägget skapades.
 */
public record PostResponseDTO(
        Long id,
        String username,
        String text,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        Long userId,
        int likeCount,
        boolean likedByMe
) {
}
