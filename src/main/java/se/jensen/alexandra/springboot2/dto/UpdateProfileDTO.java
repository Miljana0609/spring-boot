package se.jensen.alexandra.springboot2.dto;

/**
 * DTO som används för att uppdatera en användares profilinformation.
 *
 * @param displayName      - visningsnamn
 * @param bio              - biografi
 * @param profileImagePath - URL till profilbild
 */
public record UpdateProfileDTO(
        String displayName,
        String bio,
        String profileImagePath
) {
}
