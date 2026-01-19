package se.jensen.alexandra.springboot2.security;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import se.jensen.alexandra.springboot2.model.User;

import java.util.Collection;
import java.util.List;

//UserDetails finns inbyggt i Spring, vi får därmed skapa en egen som implementerar den klassen
public class MyUserDetails implements UserDetails {
    private final User user;        //Detta gör att klassen wrappar runt vår User klass och hämtar våra

    //värden av User
    public MyUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
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
    public User getDomainUser() {
        return user;
    }


    public Long getId() {
        return user.getId();
    }
}
