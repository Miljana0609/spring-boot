package se.jensen.alexandra.springboot2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
        Long id,

        @NotBlank(message = "Användarnamn får ej vara tomt.")
        @Size(min = 3, max = 20, message = "Användarnamn måste vara mellan 3-20 tecken.")
        String username,

        @Email(message = "Ogiltig e-postadress")
        String email,

        @NotBlank(message = "Lösenord måste anges.")
        @Size(min = 7, message = "Lösenord måste bestå av minst 7 tecken.")
        @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Endast bokstäver och siffror tillåtna.")
        String password,

        @NotBlank(message = "Roll får ej vara tomt.")
        String role,

        String displayName,

        String bio,

        String profileImagePath
) {
}
