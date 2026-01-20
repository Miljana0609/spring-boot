package se.jensen.alexandra.springboot2.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import se.jensen.alexandra.springboot2.dto.UserRequestDTO;
import se.jensen.alexandra.springboot2.dto.UserResponseDTO;
import se.jensen.alexandra.springboot2.mapper.UserMapper;
import se.jensen.alexandra.springboot2.model.User;
import se.jensen.alexandra.springboot2.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    //Test skapad av AI
    @Test
    void addUser_ShouldThrowException_WhenUserAlreadyExists() {
        // Arrange
        UserRequestDTO request = new UserRequestDTO(
                null, "existingUser", "email@test.com",
                "pass", "USER", "Name", "Bio",
                null);
        User user = new User();
        user.setUsername("existingUser");
        user.setEmail("email@test.com");

        when(userMapper.fromDto(request)).thenReturn(user);
        when(passwordEncoder.encode(any())).thenReturn("encodedPass");
        // Här simulerar vi att användaren redan finns i databasen
        when(userRepository.existsByUsernameOrEmail(user.getUsername(), user.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.addUser(request));
        verify(userRepository, never()).save(any()); // Verifiera att save ALDRIG anropas
    }

    //Test skapad efter instruktioner från Håkan - code along
    //Testar att vår kod i findUserById fungerar som den ska
    @Test
    void testFindUserById() {
        //Arrange
        User user = new User();
        user.setUsername("Alexandra");
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        //Act
        UserResponseDTO foundUser = userService.findUserById(1L);

        //Assert
        assertEquals("Alexandra", foundUser.username());
    }
}
