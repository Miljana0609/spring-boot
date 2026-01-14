package se.jensen.alexandra.springboot2.dto;

import org.springframework.data.domain.Page;

public record UserWithPostsResponseDTO(
        UserResponseDTO user,
        Page<PostResponseDTO> posts
) {
}
