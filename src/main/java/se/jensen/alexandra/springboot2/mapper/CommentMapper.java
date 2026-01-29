package se.jensen.alexandra.springboot2.mapper;

import org.springframework.stereotype.Component;
import se.jensen.alexandra.springboot2.dto.CommentResponseDTO;
import se.jensen.alexandra.springboot2.model.Comment;
import se.jensen.alexandra.springboot2.model.User;

/**
 * Mapper-klass som omvandlar Comment-entiteter till CommentResponseDTO som kan skickas till frontend.
 */
@Component
public class CommentMapper {

    /**
     * Skapar ett DTO-objekt från en kommentar i databasen.
     *
     * @param comment     - kommentar som ska omvandlas
     * @param currentUser - den inloggade användaren
     * @return - ett CommentResponseDTO-objekt.
     */
    public CommentResponseDTO toDto(Comment comment, User currentUser) {
        return new CommentResponseDTO(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUser().getId(),
                comment.getUser().getUsername(),
                comment.getUser().getDisplayName(),
                comment.getLikedBy().size(),
                currentUser != null && comment.getLikedBy().contains(currentUser),
                comment.getParentComment() != null ? comment.getParentComment().getId() : null
        );
    }
}
