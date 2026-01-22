package se.jensen.alexandra.springboot2.security;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import se.jensen.alexandra.springboot2.model.User;

import java.util.Collection;
import java.util.List;

/**
 * En klass som används av Spring Security vid inloggning. Den wrappar ett User-objekt och översätter
 * användarens information till ett format som Spring Security förstår.
 */
//UserDetails finns inbyggt i Spring, vi får därmed skapa en egen som implementerar den klassen
public class MyUserDetails implements UserDetails {
    private final User user;        //Detta gör att klassen wrappar runt vår User klass och hämtar våra

    //värden av User
    public MyUserDetails(User user) {
        this.user = user;
    }

    /**
     * Metoden som hämtar användarens roll i rätt format som Spring Security kräver.
     *
     * @return user.getRole - returnerar användarens roll i Spring Security-format (ADMIN/USER)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    /**
     * Metod som används av Spring Security för att kontrollera inloggning.
     *
     * @return user.getPassword - användarens lösenord
     */
    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    /**
     * Används vid inloggning
     *
     * @return user.getUsername - Returnerar användarens användarnamn
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * getDomainUser används när man behöver mer information om användaren
     *
     * @return user - Returnerar hela User-objektet
     */

    public User getDomainUser() {
        return user;
    }


    /**
     * Metod som hämtar användarens ID
     *
     * @return user.getId
     */
    public Long getId() {
        return user.getId();
    }


    //Metoder som inte behövs just nu, kommer expandera och använda dessa framåt
//    @Override
//    public boolean isAccountNonExpired() {
//        return UserDetails.super.isAccountNonExpired();
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return UserDetails.super.isAccountNonLocked();
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return UserDetails.super.isCredentialsNonExpired();
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return UserDetails.super.isEnabled();
//    }
//
}
