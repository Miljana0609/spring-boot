package se.jensen.alexandra.springboot2.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import se.jensen.alexandra.springboot2.dto.CommentRequestDTO;
import se.jensen.alexandra.springboot2.dto.CommentResponseDTO;
import se.jensen.alexandra.springboot2.mapper.CommentMapper;
import se.jensen.alexandra.springboot2.model.Comment;
import se.jensen.alexandra.springboot2.model.Post;
import se.jensen.alexandra.springboot2.model.User;
import se.jensen.alexandra.springboot2.repository.CommentRepository;
import se.jensen.alexandra.springboot2.repository.PostRepository;
import se.jensen.alexandra.springboot2.repository.UserRepository;

import java.util.NoSuchElementException;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;


    public CommentService(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentMapper = commentMapper;
    }

    public CommentResponseDTO createComment(
            CommentRequestDTO dto, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Kunde inte hitta användare"));

        Post post = postRepository.findById(dto.postId())
                .orElseThrow(() -> new NoSuchElementException("Kunde inte hitta inlägg"));

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent(dto.content());

        if (dto.parentCommentId() != null) {
            Comment parent = commentRepository.findById(dto.parentCommentId())
                    .orElseThrow();
            comment.setParentComment(parent);
        }
        Comment saved = commentRepository.save(comment);
        return commentMapper.toDto(saved, user);
    }

    public Page<CommentResponseDTO> getCommentsForPost(
            Long postId, Pageable pageable, String username) {

        User currentUser = userRepository.findByUsername(username).orElse(null);

        return commentRepository.findByPostIdAndParentCommentIsNull(postId, pageable)
                .map(c -> commentMapper.toDto(c, currentUser));
    }

    public void toggleLike(Long commentId, String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Comment comment = commentRepository.findById(commentId).orElseThrow();

        if (comment.getLikedBy().contains(user)) {
            comment.getLikedBy().remove(user);
        } else {
            comment.getLikedBy().add(user);
        }
        commentRepository.save(comment);
    }

    public CommentResponseDTO updateComment(Long commentId, String newContent, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Kommentaren hittades inte"));

        if (!comment.getUser().getUsername().equals(username)) {
            throw new org.springframework.security.access.AccessDeniedException("Du kan ändra den här kommentaren.");
        }
        comment.setContent(newContent);
        Comment updated = commentRepository.save(comment);

        User user = userRepository.findByUsername(username).orElseThrow();
        return commentMapper.toDto(updated, user);

    }

    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Kommentaren hittade inte med id: " + commentId));

        //Säkerhetskontroll att användaren "äger" kommentaren
        if (!comment.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("Du kan inte ta bort den här kommentaren.");
        }
        commentRepository.delete(comment);
    }
}
