package se.jensen.alexandra.springboot2.dto;

/**
 * En DTO som används för att skicka tillbaka statusen för en vänskapsrelation.
 *
 * @param status       - relationens status (NONE, PENDING, ACCEPTED, REJECTED)
 * @param friendshipId - ID för relationen, eller null om ingen relation finns
 * @param requesterId  - ID för användaren som skickade vänförfrågan
 * @param receiverId   - ID för användaren som mottog vänförfrågan
 */
public record FriendshipStatusResponseDTO(
        String status,
        Long friendshipId,
        Long requesterId,
        Long receiverId
) {
}
