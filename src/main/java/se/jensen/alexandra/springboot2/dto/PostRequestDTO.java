package se.jensen.alexandra.springboot2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostRequestDTO(
        @NotBlank(message = "Texten får inte vara tom.")
        @Size(min = 3, max = 200, message = "Texten måste vara mellan 3-200 tecken.")
        String text
) {
}
