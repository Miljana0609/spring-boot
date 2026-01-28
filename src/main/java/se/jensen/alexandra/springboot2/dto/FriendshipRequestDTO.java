package se.jensen.alexandra.springboot2.dto;

/**
 * En DTO som används för att ta emot data när en vänförfrågan skickas från frontend.
 *
 * @param requesterId - ID för användaren som skickar vänförfrågan
 * @param receiverId  - ID för användaren som tar emot vänförfrågan
 */

public record FriendshipRequestDTO(
        Long requesterId,
        Long receiverId
) {
}
