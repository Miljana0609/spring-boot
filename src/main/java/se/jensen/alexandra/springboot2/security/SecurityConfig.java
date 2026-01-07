package se.jensen.alexandra.springboot2.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean   //SecurityFilterChain är en inbyggd metod som finns i Spring security
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);     //Ett säkerhetstoken som vi nu har valt att stänga av

        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
                        .requestMatchers(   //Raden ovan och de tre nedan öppnar upp för att vem som helst ska kunna
                                "/swagger-ui/**",   //gå in och skapa en ny user, detta för att vi i övrigt låst applikationen
                                "/v3/api-docs/**",  //Vi kan därmed skapa ett konto som har Admin för att sedan låsa framöver
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/public/**").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())   //Används för att test och front-end ska kunna ha åtkomst till applikationen
                .formLogin(Customizer.withDefaults());  //Ett loginformulär krävs för att logga in och kunna se/göra ändringar på info
        return http.build();
    }

    @Bean       //Inbyggd metod som hashar lösenord med BCrypt
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
