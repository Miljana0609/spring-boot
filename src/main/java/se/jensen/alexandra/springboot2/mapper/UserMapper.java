package se.jensen.alexandra.springboot2.mapper;

import org.springframework.stereotype.Component;
import se.jensen.alexandra.springboot2.dto.UserRequestDTO;
import se.jensen.alexandra.springboot2.dto.UserResponseDTO;
import se.jensen.alexandra.springboot2.model.User;

@Component
public class UserMapper {

    public User fromDto(UserRequestDTO dto) {
        User user = new User();
        setUserValues(user, dto);
        return user;
    }

    public User fromDto(User user, UserRequestDTO dto) {
        setUserValues(user, dto);
        return user;
    }

    private void setUserValues(User user, UserRequestDTO dto) {
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setRole(dto.role());
        user.setDisplayName(dto.displayName());
        user.setBio(dto.bio());
        user.setProfileImagePath(dto.profileImagePath());
    }

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
