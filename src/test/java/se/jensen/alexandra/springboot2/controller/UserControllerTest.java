package se.jensen.alexandra.springboot2.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import se.jensen.alexandra.springboot2.dto.UserResponseDTO;
import se.jensen.alexandra.springboot2.model.User;
import se.jensen.alexandra.springboot2.repository.UserRepository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Disabled
public class UserControllerTest {

    @Autowired      //Används då man inte har en konstruktor i test.
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();

        User user = new User();
        user.setRole("ADMIN");
        user.setPassword(passwordEncoder.encode("password"));
        user.setUsername("admin");
        user.setEmail("admin@example.com");
        user.setDisplayName("Admin display");
        user.setBio("Admin bio");
        user.setProfileImagePath("**");
        userRepository.save(user);
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        //1. Arrange
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .with(httpBasic("admin", "password")))
                .andExpect(status().isOk()).andReturn();
        //2. Konvertera
        String response = result.getResponse().getContentAsString();

        //3. Act + Assert
        List<UserResponseDTO> users = objectMapper.readValue(
                response, new TypeReference<List<UserResponseDTO>>() {
                });

        //4. Assert
        assertEquals(1, users.size());
    }

    @Test
    void shouldFindUserById() throws Exception {
        shouldCreateUser();
        User existing = userRepository.findAll().get(1);

        //2. Act
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/users/" + existing.getId())
                        .with(httpBasic("admin", "password")))
                .andExpect(status().isOk()).andReturn();

        String response = result.getResponse().getContentAsString();

        //3. Assert
        UserResponseDTO savedUser = objectMapper.readValue(response, UserResponseDTO.class);
        assertEquals("testuser", savedUser.username());
        assertEquals("testuser@example.com", savedUser.email());
    }

    @Test
    void shouldCreateUser() throws Exception {
        //1. Arrange
        String requestDTO = """
                {
                "username": "testuser",
                "email": "testuser@example.com",
                "password": "password",
                "role": "USER",
                "displayName": "Test User display",
                "bio": "Test User bio",
                "profileImagePath": "**"
                }
                """;

        //2. Act
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .with(httpBasic("admin", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDTO))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        //3. Assert
        List<User> users = userRepository.findAll();
        assertEquals(2, users.size());

        User savedUser = users.get(1);
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("testuser@example.com", savedUser.getEmail());

        assertTrue(passwordEncoder.matches("password", savedUser.getPassword()));
    }


}
