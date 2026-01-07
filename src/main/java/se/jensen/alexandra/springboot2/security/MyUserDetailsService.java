package se.jensen.alexandra.springboot2.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import se.jensen.alexandra.springboot2.model.User;
import se.jensen.alexandra.springboot2.repository.UserRepository;

@Service        //UserDetailsService finns i Spring security, vi skapar Myxxx för att implementera den klassen
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override       //Inbyggd metod som finns, där vi lägger in var den ska leta efter userame osv.
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new MyUserDetails(user);
    }
}
