package se.jensen.alexandra.springboot2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record PostResponseDTO(
        Long id,
        String username,
        String text,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        Long userId
) {
}
