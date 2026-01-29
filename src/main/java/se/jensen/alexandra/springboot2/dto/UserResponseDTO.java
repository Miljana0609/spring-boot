package se.jensen.alexandra.springboot2.dto;

/**
 * En klass som används när man skickar tillbaka användarinformation till frontend utan lösenord.
 *
 * @param id               - users id
 * @param username         - användarnamn
 * @param email            - e-postadress
 * @param role             - roll (ADMIN/USER)
 * @param displayName      - visningsnamn
 * @param bio              - beskrivning
 * @param profileImagePath - bild
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
