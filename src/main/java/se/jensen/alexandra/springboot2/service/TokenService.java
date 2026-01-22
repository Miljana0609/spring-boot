package se.jensen.alexandra.springboot2.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

/**
 * En serviceklass som ansvarar för att skapa säkerhetstoken (JWT).
 * Den används när en användare har loggat in för att generera en token som bevisar vem användaren är och vilka rättigheter den har i systemet.
 */
@Service
public class TokenService {
    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    private final JwtEncoder jwtEncoder;

    public TokenService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    /**
     * Metod som skapar en JWT-token för en inloggad användare. Den tar emot information om användaren och dess roller,
     * sätter hur länge den ska vara giltig (1 timme) och returnerar den färdiga token som en sträng.
     * Tokenen används sen för att ge användaren åtkomst till skyddade resurser.
     *
     * @param authentication - Information om den autentiserade användaren
     * @return token - String som ger åtkomst
     */
    public String generateToken(Authentication authentication) {
        logger.info("Genererar token för användare: {}", authentication.getName());
        Instant now = Instant.now();

        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();

        String token = jwtEncoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
        logger.debug("Token genererad framgångsrikt");
        return token;
    }
}
