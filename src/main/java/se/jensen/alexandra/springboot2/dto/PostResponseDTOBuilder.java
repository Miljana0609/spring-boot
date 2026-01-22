package se.jensen.alexandra.springboot2.dto;

import java.time.LocalDateTime;

public final class PostResponseDTOBuilder {
    private Long id;
    private String username;
    private String text;
    private LocalDateTime createdAt;
    private Long userId;

    private PostResponseDTOBuilder() {
    }

    public static PostResponseDTOBuilder builder() {
        return new PostResponseDTOBuilder();
    }

    public PostResponseDTOBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public PostResponseDTOBuilder username(String username) {
        this.username = username;
        return this;
    }

    public PostResponseDTOBuilder text(String text) {
        this.text = text;
        return this;
    }

    public PostResponseDTOBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public PostResponseDTOBuilder userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public PostResponseDTO build() {
        return new PostResponseDTO(id, username, text, createdAt, userId);
    }

    public static PostResponseDTOBuilder from(PostResponseDTO dto) {
        return builder()
                .id(dto.id())
                .username(dto.username())
                .text(dto.text())
                .createdAt(dto.createdAt())
                .userId(dto.userId());
    }
}
