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

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
    @Mock
    private JwtEncoder jwtEncoder;

    @InjectMocks
    private TokenService tokenService;

    @Test
    void generateToken_ShouldReturnValidTokenString() {
        // Arrange
        Authentication auth = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);

        when(auth.getName()).thenReturn("testuser");
        when(auth.getAuthorities()).thenReturn(Collections.emptyList());
        when(jwt.getTokenValue()).thenReturn("mocked-token-string");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        // Act
        String result = tokenService.generateToken(auth);

        // Assert
        assertEquals("mocked-token-string", result);
        // Verifiera att jwtEncoder faktiskt anropades
        verify(jwtEncoder, times(1)).encode(any(JwtEncoderParameters.class));
    }
}
