package se.jensen.alexandra.springboot2.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import se.jensen.alexandra.springboot2.model.Friendship;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    //Kontrollerar om relation redan finns, oavsett riktning
    boolean existsByRequesterIdAndReceiverId(Long requesterId, Long receiverId);

    boolean existsByReceiverIdAndRequesterId(Long receiverId, Long requesterId);

    //Listor som hämtar antingen inkommande, godkända eller skickade vänskapsförfrågningar
    Page<Friendship> findByRequesterIdAndStatus(Long requesterId, Friendship.Status status, Pageable pageable);

    Page<Friendship> findByReceiverIdAndStatus(Long receiverId, Friendship.Status status, Pageable pageable);

    List<Friendship> findByRequesterIdAndStatus(Long requesterId, Friendship.Status status);

    List<Friendship> findByReceiverIdAndStatus(Long receiverId, Friendship.Status status);

    List<Friendship> findByRequesterIdOrReceiverId(Long requesterId, Long receiverId);

    @Query("""
                SELECT f FROM Friendship f
                WHERE (f.requester.id = :u1 AND f.receiver.id = :u2)
                   OR (f.requester.id = :u2 AND f.receiver.id = :u1)
            """)
    Optional<Friendship> findByUsers(Long u1, Long u2);
}
