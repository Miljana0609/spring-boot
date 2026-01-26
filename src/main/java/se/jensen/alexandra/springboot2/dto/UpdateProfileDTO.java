package se.jensen.alexandra.springboot2.dto;

public record UpdateProfileDTO(
        String displayName,
        String bio,
        String profileImagePath
) {
}
