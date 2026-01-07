package se.jensen.alexandra.springboot2.dto;

import java.util.List;

public record UserWithPostsResponseDTO(
        UserResponseDTO user,
        List<PostResponseDTO> posts
) {
}
