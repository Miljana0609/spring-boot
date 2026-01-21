package se.jensen.alexandra.springboot2.dto;

/**
 * En klass som används när man skickar tillbaka användarinformation till frontend utan lösenord.
 *
 * @param id
 * @param username
 * @param email
 * @param role
 * @param displayName
 * @param bio
 * @param profileImagePath
 */
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
