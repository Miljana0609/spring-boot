package se.jensen.alexandra.springboot2.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testklass för att testa funktionaliteten hos TokenService.
 * Klassen använder mockade beroenden för att simulera interaktioner med JwtEncoder och Authentication.
 */
@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;  // Mock av JwtEncoder för att simulera JWT-kodning.

    @InjectMocks
    private TokenService tokenService;  // Den klass som testas, TokenService, där mockade beroenden injiceras.

    /**
     * Testar att metoden generateToken returnerar en giltig token-sträng.
     * Den simulerar en situation där en JWT token genereras för en autentiserad användare.
     *
     * @throws Exception om något går fel under testets körning.
     */
    @Test
    void generateToken_ShouldReturnValidTokenString() {
        // Arrange
        Authentication auth = mock(Authentication.class);  // Mock av Authentication för att simulera användarens inloggning.
        Jwt jwt = mock(Jwt.class);  // Mock av Jwt för att simulera en genererad token.

        // Simulerar att användarnamnet är "testuser" och att användaren har inga speciella roller eller rättigheter.
        when(auth.getName()).thenReturn("testuser");
        when(auth.getAuthorities()).thenReturn(Collections.emptyList());

        // Simulerar att JwtEncoder returnerar en mockad JWT-token.
        when(jwt.getTokenValue()).thenReturn("mocked-token-string");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        // Act
        // Anropar den metod som genererar token.
        String result = tokenService.generateToken(auth);

        // Assert
        // Verifierar att den genererade token är korrekt och att metoden utförs som förväntat.
        assertEquals("mocked-token-string", result);

        // Verifierar att JwtEncoder-metoden encode anropades exakt en gång.
        verify(jwtEncoder, times(1)).encode(any(JwtEncoderParameters.class));
    }
}
