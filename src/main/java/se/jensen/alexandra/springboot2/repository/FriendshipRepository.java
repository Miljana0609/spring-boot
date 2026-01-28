package se.jensen.alexandra.springboot2.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import se.jensen.alexandra.springboot2.model.Friendship;

import java.util.List;
import java.util.Optional;

/**
 * Interface som används för att kommunicera med databasen för vänskapsrelationer.
 * Det ärver från JpaRepository, vilket innebär att standardfunktioner för att spara,
 * hämta, uppdatera och ta bort relationer redan finns färdiga.
 */
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    /**
     * Kontrollerar om en relation redan finns i given riktning.
     *
     * @param requesterId - ID för användaren som skickat förfrågan
     * @param receiverId  - ID för användaren som mottagit förfrågan
     * @return true om relationen finns, annars false
     */
    boolean existsByRequesterIdAndReceiverId(Long requesterId, Long receiverId);

    /**
     * Kontrollerar om en relation finns i motsatt riktning.
     *
     * @param receiverId  - ID för mottagaren
     * @param requesterId - ID för avsändaren
     * @return true om relationen finns, annars false
     */
    boolean existsByReceiverIdAndRequesterId(Long receiverId, Long requesterId);

    /**
     * Hämtar skickade vänförfrågningar med angiven status.
     *
     * @param requesterId - ID för avsändaren
     * @param status      - relationens status
     * @param pageable    - pagineringsinställningar
     * @return en sida med matchande relationer
     */
    Page<Friendship> findByRequesterIdAndStatus(Long requesterId, Friendship.Status status, Pageable pageable);

    /**
     * Hämtar mottagna vänförfrågningar med angiven status.
     *
     * @param receiverId - ID för mottagaren
     * @param status     - relationens status
     * @param pageable   - pagineringsinställningar
     * @return en sida med matchande relationer
     */
    Page<Friendship> findByReceiverIdAndStatus(Long receiverId, Friendship.Status status, Pageable pageable);

    /**
     * Hämtar skickade vänförfrågningar utan pagination.
     *
     * @param requesterId - ID för avsändaren
     * @param status      - relationens status
     * @return lista med matchande relationer
     */
    List<Friendship> findByRequesterIdAndStatus(Long requesterId, Friendship.Status status);

    /**
     * Hämtar mottagna vänförfrågningar utan pagination.
     *
     * @param receiverId - ID för mottagaren
     * @param status     - relationens status
     * @return lista med matchande relationer
     */
    List<Friendship> findByReceiverIdAndStatus(Long receiverId, Friendship.Status status);

    /**
     * Hämtar alla relationer där användaren är antingen avsändare eller mottagare.
     *
     * @param requesterId - ID för avsändaren
     * @param receiverId  - ID för mottagaren
     * @return lista med relationer
     */
    List<Friendship> findByRequesterIdOrReceiverId(Long requesterId, Long receiverId);

    /**
     * Hämtar relation mellan två användare oavsett riktning.
     *
     * @param u1 - första användarens ID
     * @param u2 - andra användarens ID
     * @return Optional med relationen om den finns
     */
    @Query("""
                SELECT f FROM Friendship f
                WHERE (f.requester.id = :u1 AND f.receiver.id = :u2)
                   OR (f.requester.id = :u2 AND f.receiver.id = :u1)
            """)
    Optional<Friendship> findByUsers(Long u1, Long u2);
}
