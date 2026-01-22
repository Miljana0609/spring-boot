package se.jensen.alexandra.springboot2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * En DTO klass för registrering av nya användare.
 *
 * @param username - användarnamn - ska vara mellan 3 och 50 tecken
 * @param password - lösenord - minst 7 tecken
 * @param email    - e-postadress
 */
public record RegisterUserRequestDTO(
        @NotBlank
        @Size(min = 3, max = 50)
        String username,

        @NotBlank
        @Size(min = 7)
        String password,

        @Email
        @NotBlank
        String email
) {
}
