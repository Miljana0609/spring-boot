package se.jensen.alexandra.springboot2.dto;

/**
 * En klass som används för att ta emot inloggningsinformation från frontend.
 *
 * @param username - användarens namn
 * @param password - användarens lösenord
 */
public record LoginRequestDTO(String username, String password) {

}
