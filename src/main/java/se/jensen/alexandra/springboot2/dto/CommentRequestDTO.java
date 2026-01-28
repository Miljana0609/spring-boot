package se.jensen.alexandra.springboot2.dto;

public record CommentRequestDTO(
        Long postId,
        String content,
        Long parentCommentId
) {
}
