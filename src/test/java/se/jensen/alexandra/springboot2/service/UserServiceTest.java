package se.jensen.alexandra.springboot2.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import se.jensen.alexandra.springboot2.dto.UserRequestDTO;
import se.jensen.alexandra.springboot2.dto.UserResponseDTO;
import se.jensen.alexandra.springboot2.dto.UserResponseDTOBuilder;
import se.jensen.alexandra.springboot2.mapper.UserMapper;
import se.jensen.alexandra.springboot2.model.User;
import se.jensen.alexandra.springboot2.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testklass för att testa funktionaliteten hos UserService.
 * Klassen använder mockade beroenden för att simulera interaktioner med UserRepository, UserMapper och PasswordEncoder.
 */

@ExtendWith(MockitoExtension.class)
@Disabled
class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

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

    //Test skapad efter instruktioner från Håkan - code along
    //Testar att vår kod i getAllUsers fungerar som den ska
    @Test
    void testGetAllUsers() {
        //Arrange
        //Mockar getAllUsers från UserService (List<User> users = userRepository.findAll();)
        User user = new User();
        user.setId(1L);
        user.setUsername("Alexandra");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("Håkan");

        List<User> users = List.of(user, user2);

        //Mockar metoden userMapper.toDto för att returnera rätt DTOs (return users.stream().map(userMapper::toDto).toList();)
        UserResponseDTO dto1 = UserResponseDTOBuilder.builder()
                .id(1L)
                .username("Alexandra")
                .build();
        UserResponseDTO dto2 = UserResponseDTOBuilder.builder()
                .id(2L)
                .username("Håkan")
                .build();

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDto(user)).thenReturn(dto1);
        when(userMapper.toDto(user2)).thenReturn(dto2);

        //Act
        List<UserResponseDTO> result = userService.getAllUsers();

        //Assert
        assertEquals(2, users.size());
        assertEquals("Alexandra", users.get(0).getUsername());
        assertEquals("Håkan", users.get(1).getUsername());
    }

    @Test
    void testGetAllUsers_NoUsersFound() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> userService.getAllUsers());
    }
}
