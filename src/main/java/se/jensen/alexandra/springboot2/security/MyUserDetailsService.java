package se.jensen.alexandra.springboot2.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import se.jensen.alexandra.springboot2.model.User;
import se.jensen.alexandra.springboot2.repository.UserRepository;

/**
 * En serviceklass som används av Spring Security vid inloggning. Den ansvarar för att hämta användarinformation
 * från databasen när någon försöker logga in. Klassen använder UserRepository för att hitta användaren
 * baserat på användarnamn.
 */
@Service        //UserDetailsService finns i Spring security, vi skapar Myxxx för att implementera den klassen
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Denna metod körs automatiskt när en användare försöker logga in. Den tar emot ett användarnamn och
     * returnerar användarens information i ett format som Spring Security kan använda.
     * Om användaren inte finns kastas ett fel
     *
     * @param username - Användarens användarnamn
     * @return MyUserDetails - Användarinfo i ett format som Spring Security kan förstå.
     * @throws UsernameNotFoundException - Kastas om användaren inte hittas
     */
    @Override       //Inbyggd metod som finns, där vi lägger in var den ska leta efter userame osv.
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new MyUserDetails(user);
    }
}
