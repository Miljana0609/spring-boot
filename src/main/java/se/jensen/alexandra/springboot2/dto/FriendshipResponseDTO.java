package se.jensen.alexandra.springboot2.dto;

/**
 * En DTO som används för att skicka tillbaka information om en vänskapsrelation till frontend.
 *
 * @param id        - unikt ID för vänskapsrelationen
 * @param requester - användaren som skickade vänförfrågan
 * @param receiver  - användaren som mottog vänförfrågan
 * @param status    - relationens status (t.ex. PENDING, ACCEPTED, REJECTED)
 */
public record FriendshipResponseDTO(
        Long id,
        UserResponseDTO requester,
        UserResponseDTO receiver,
        String status
) {
}
