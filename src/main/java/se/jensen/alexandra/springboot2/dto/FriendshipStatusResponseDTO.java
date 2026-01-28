package se.jensen.alexandra.springboot2.dto;

public record FriendshipStatusResponseDTO(
        String status,
        Long friendshipId,
        Long requesterId,
        Long receiverId
) {
}
