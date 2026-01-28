package se.jensen.alexandra.springboot2.dto;

import java.time.LocalDateTime;

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
