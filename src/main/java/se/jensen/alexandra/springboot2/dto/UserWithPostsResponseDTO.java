package se.jensen.alexandra.springboot2.dto;

import org.springframework.data.domain.Page;

/**
 * En klass som används när man vill skicka både användarinformation
 * och deras inlägg till frontend.
 *
 * @param user  - ett UserResponseDTO-objekt med användarens info
 * @param posts - en sida (Page) med PostResponseDTO-objekt som visar
 *              alla inlägg som den användare har gjort.
 */
public record UserWithPostsResponseDTO(
        UserResponseDTO user,
        Page<PostResponseDTO> posts
) {
}
