package se.jensen.alexandra.springboot2.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import se.jensen.alexandra.springboot2.model.Post;

/**
 * Repository-interface som används för att kommunicera med databasen för inlägg (posts).
 * Det ärver från JpaRepository, vilket innebär att funktionerna som att spara, uppdatera och ta bort inlägg redan finns färdiga.
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * Metoden används för att hämta alla inlägg som tillhör en användare baserat på användarens ID, med stöd för paginering.
     *
     * @param userId   - Användarens ID
     * @param pageable - Information om sortering
     * @return Page<Post> - En sida med användarens inlägg
     */
    Page<Post> findByUserId(Long userId, Pageable pageable);
}
