package se.jensen.alexandra.springboot2.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.jensen.alexandra.springboot2.dto.LoginRequestDTO;
import se.jensen.alexandra.springboot2.dto.LoginResponseDTO;
import se.jensen.alexandra.springboot2.security.MyUserDetails;
import se.jensen.alexandra.springboot2.service.TokenService;

/**
 * En REST-controller som hanterar autentisering (inloggning) i applikationen och skapar
 * JWT-token som frontend kan använda för att göra säkra anrop.
 * Alla anrop till denna controller börjar med /request-token.
 */
@RestController
@RequestMapping("/request-token")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthController(AuthenticationManager authenticationManager,
                          TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    /**
     * Tar emot användarnamn och lösenord från frontend och kontrollerar att uppgifterna stämmer.
     * Hämtar användarens ID från MyUserDetails.
     * Skapar ett JWT-token med TokenService
     *
     * @param loginRequestDTO - innehåller användarnamn och lösenord
     * @return - LoginResponseDTO - returnerar token och användarens ID
     */
    @PostMapping
    public ResponseEntity<LoginResponseDTO> token(
            @RequestBody LoginRequestDTO loginRequestDTO) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.username(),
                        loginRequestDTO.password()
                )
        );
        MyUserDetails details = (MyUserDetails) auth.getPrincipal();
        details.getId();

        String token = tokenService.generateToken(auth);

        return ResponseEntity.ok(new LoginResponseDTO(token, details.getId()));
    }
}
