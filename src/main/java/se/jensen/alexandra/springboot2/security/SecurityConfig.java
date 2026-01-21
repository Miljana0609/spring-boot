package se.jensen.alexandra.springboot2.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

/**
 * SecurityConfig är en konfigurationsklass som hanterar säkerheten i applikationen.
 * Den kontrollerar vem som får åtkomst till olika endpoints, hur användare autentiseras med JWT-token,
 * hur lösenord sparas säkert och vilka frontend-sidor som får prata med backend.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    /**
     * Metod som bestämmer vilka sidor som är öppna för alla och vilka som kräver roller (t.ex. ADMIN).
     * Ställer också in JWT-autentisering, CORS och sessioner.
     *
     * @param http - Objekt som används för att konfigurera säkerheten
     * @return SecurityFilterChain - Färdig säkerhetskonfiguration
     * @throws Exception
     */
    @Bean   //SecurityFilterChain är en inbyggd metod som finns i Spring security
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));     //Cross-Origin Resource Sharing
        http.csrf(AbstractHttpConfigurer::disable);     //Ett säkerhetstoken som vi nu har valt att stänga av

        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt ->
                        jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/request-token").permitAll()
                .requestMatchers(HttpMethod.POST, "/users").permitAll()
                .requestMatchers(//Raden ovan och de tre nedan öppnar upp för att vem som helst ska kunna
                        "/swagger-ui/**",   //gå in och skapa en ny user, detta för att vi i övrigt låst applikationen
                        "/v3/api-docs/**",  //Vi kan därmed skapa ett konto som har Admin för att sedan låsa framöver
                        "/swagger-ui.html"
                ).permitAll() //Hoppas det uppdateras nu
                .requestMatchers("/public/**").permitAll()

                .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated());
        return http.build();
    }

    /**
     * Metod som gör så att användarnas lösenord lagras säkert med hashning (BCrypt).
     *
     * @return BCryptPasswordEncoder för att hash:a användarlösenord.
     */
    @Bean       //Inbyggd metod som hashar lösenord med BCrypt
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Metod som skapar RSA-nyckelpar från Base64, som används för att skapa och kontrollera JWT-tokens.
     *
     * @param privateKey - Privat nyckel i Base64-format
     * @param publicKey  - Publik nyckel i Base64-format
     * @return new KeyPair - Ett nyckelpar med privat och publik nyckel
     * @throws Exception - Om nycklarna inte kan skapas
     */
    @Bean
    public KeyPair keyPair(
            @Value("${jwt.private-key}") String privateKey,
            @Value("${jwt.public-key}") String publicKey
    ) throws Exception {

        byte[] privateBytes = Base64.getDecoder().decode(privateKey);
        byte[] publicBytes = Base64.getDecoder().decode(publicKey);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        PrivateKey privKey = keyFactory.generatePrivate(
                new PKCS8EncodedKeySpec(privateBytes)
        );

        PublicKey pubKey = keyFactory.generatePublic(
                new X509EncodedKeySpec(publicBytes)
        );

        return new KeyPair(pubKey, privKey);
    }

    /**
     * Metod som skapar en källa av nycklar (JWK) som används för att signera JWT-tokens.
     *
     * @param keyPair - Nyckelpar som innehåller privat och public nyckel
     * @return JWKSource - Källa som innehåller nycklar för JWT-signering
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource(KeyPair keyPair) {
        RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey((RSAPrivateKey) keyPair.getPrivate())
                .keyID("jwt-key-1")
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, context) -> jwkSelector.select(jwkSet);
    }

    /**
     * Metod som skapar JWT-tokens som skickas till användare vid inloggning.
     *
     * @param jwkSource - Källa som innehåller nycklar för signering
     * @return NimbusJwtEncoder - Encoder som skapar JWT-tokens
     */
    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    /**
     * Metod som kontrollerar att JWT-tokens som skickas tillbaka är giltiga.
     *
     * @param keyPair - Nyckelpar som innehåller privat och public nyckel
     * @return NimbusJwtDecoder - Decoder som verifierar JWT-tokens
     */
    @Bean
    public JwtDecoder jwtDecoder(KeyPair keyPair) {
        return NimbusJwtDecoder
                .withPublicKey((RSAPublicKey) keyPair.getPublic())
                .build();
    }

    /**
     * Metod som gör om informationen i JWT-token till roller och rättigheter som Spring Security kan förstå.
     *
     * @return JwtAuthenticationConverter - Converter för JWT-autentisering
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter converter =
                new JwtGrantedAuthoritiesConverter();

        converter.setAuthorityPrefix("");
        converter.setAuthoritiesClaimName("scope");

        JwtAuthenticationConverter authenticationConverter =
                new JwtAuthenticationConverter();

        authenticationConverter.setJwtGrantedAuthoritiesConverter(converter);
        return authenticationConverter;
    }

    /**
     * Metod som returnerar ett autentiseringsobjekt som används för att
     * autentisera användare vid inloggning
     *
     * @param configuration - Spring Securitys autentiseringskonfiguration
     * @return AuthenticationManager - Objekt som hanterar autentisering
     * @throws Exception - Om AuthenticationManager inte kan skapas
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Metod som konfigurerar CORS-inställningar, alltså vilka domäner,
     * HTTP-metoder och headers som frontend får använda.
     *
     * @return CorsConfigurationSource - Konfiguration för CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:5175",
                "http://localhost:3000"
                //TODO: öppna upp för frontend på koyeb
                //ungefär: "https://your-frontend-domain.com"
        ));

        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

}
