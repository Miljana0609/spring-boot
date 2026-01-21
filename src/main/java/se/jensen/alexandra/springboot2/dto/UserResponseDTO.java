package se.jensen.alexandra.springboot2.dto;

public record UserResponseDTO(
        Long id,
        String username,
        String email,
        String role,
        String displayName,
        String bio,
        String profileImagePath
) {


}
