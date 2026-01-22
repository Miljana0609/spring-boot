package se.jensen.alexandra.springboot2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import se.jensen.alexandra.springboot2.model.User;

import java.util.Optional;

/**
 * Interface som används för att kommunicera med databasen för användare.
 * Det ärver från JpaRepository, vilket innebär att funktioner som att spara, hämta, uppdatera och ta bort
 * användare redan finns färdiga.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Kontrollerar om det redan finns en användare med angivet användarnamn eller e-postadress.
     *
     * @param username - Användarnamn
     * @param email    - e-postadress
     * @return true om användarens finns, annars false
     */
    boolean existsByUsernameOrEmail(String username, String email);

    /**
     * Hämtar en användare tillsammans med inlägg
     *
     * @param id - Användarens ID
     * @return Optional - med användare och tillhörande inlägg
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.posts WHERE u.id = :id")
    Optional<User> getUserWithPosts(Long id);

    /**
     * Hämtar en användare via användarnamn.
     *
     * @param username - Användarens användarnamn
     * @return Optional - med användare
     */
    Optional<User> findByUsername(String username);
}
