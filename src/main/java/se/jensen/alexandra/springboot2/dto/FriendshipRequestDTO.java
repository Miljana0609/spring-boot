package se.jensen.alexandra.springboot2.dto;

public record FriendshipRequestDTO(
        Long requesterId,
        Long receiverId
) {
}
