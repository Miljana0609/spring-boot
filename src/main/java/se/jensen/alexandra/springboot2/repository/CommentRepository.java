package se.jensen.alexandra.springboot2.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import se.jensen.alexandra.springboot2.model.Comment;

/**
 * Repository som används för att spara, hämta och ta bort kommentarer i databasen.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Hämtar alla huvudkommentarer för ett inlägg (utan parentComment)
     *
     * @param postId   - inläggets id
     * @param pageable - information om paginering
     * @return - en sida med kommentarer
     */
    Page<Comment> findByPostIdAndParentCommentIsNull(
            Long postId, Pageable pageable);

//    List<Comment> findByParentCommentIdOrderByCreatedAtAsc(Long parentId);
}
