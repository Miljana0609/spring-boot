package se.jensen.alexandra.springboot2.dto;

public record FriendshipResponseDTO(
        Long id,
        UserResponseDTO requester,
        UserResponseDTO receiver,
        String status
) {
}
