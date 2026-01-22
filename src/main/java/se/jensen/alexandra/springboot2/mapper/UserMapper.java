package se.jensen.alexandra.springboot2.mapper;

import org.springframework.stereotype.Component;
import se.jensen.alexandra.springboot2.dto.UserRequestDTO;
import se.jensen.alexandra.springboot2.dto.UserResponseDTO;
import se.jensen.alexandra.springboot2.model.User;

/**
 * En hjälpklass som används för att mappa (kopiera) data mellan User och UserRequestDTO/UserResponseDTO.
 * Den gör det lättare att skicka rätt data mellan databasen, backend och frontend.
 * Används både för att skapa nya användare och uppdatera befintliga.
 */
@Component
public class UserMapper {

    /**
     * Används när man skapar en ny användare.
     *
     * @param dto - Information om användaren
     * @return User - den skapade användare
     */
    public User fromDto(UserRequestDTO dto) {
        User user = new User();
        setUserValues(user, dto);
        return user;
    }

    /**
     * Metoden uppdaterar en befintlig User med nya värden från DTO.
     *
     * @param user - befintlig användare som ska uppdateras
     * @param dto  - Information att uppdatera användaren med
     * @return User - den uppdaterade User
     */
    public User fromDto(User user, UserRequestDTO dto) {
        setUserValues(user, dto);
        return user;
    }

    /**
     * Privat hjälpmetod som sätter alla fält på User från DTO. Används av båda fromDto-metoderna.
     * Hjälper att koden blir renare och mindre upprepande.
     *
     * @param user - användaren som ska uppdateras
     * @param dto  - data att sätta på användaren
     */
    private void setUserValues(User user, UserRequestDTO dto) {
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setRole(dto.role());
        user.setDisplayName(dto.displayName());
        user.setBio(dto.bio());
        user.setProfileImagePath(dto.profileImagePath());
    }

    /**
     * Metod som skapar ett UserResponseDto från en User. Används när man hämtar och visar användardata och
     * skickar tillbaka till frontend.
     *
     * @param user - Användaren som ska omvandlas
     * @return UserResponseDTO - DTO med användarens information
     */
    public UserResponseDTO toDto(User user) {
        UserResponseDTO dto = new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getDisplayName(),
                user.getBio(),
                user.getProfileImagePath());
        return dto;
    }
}
