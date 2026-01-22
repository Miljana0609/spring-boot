package se.jensen.alexandra.springboot2.dto;

/**
 * En klass som används för att skicka tillbaka svar efter en lyckad inloggning.
 *
 * @param token  - JWT-token som används för autentisering
 * @param userId - ID för den inloggade användaren
 */
public record LoginResponseDTO(String token, Long userId) {
}
