package se.jensen.alexandra.springboot2.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.jensen.alexandra.springboot2.dto.*;
import se.jensen.alexandra.springboot2.mapper.PostMapper;
import se.jensen.alexandra.springboot2.mapper.UserMapper;
import se.jensen.alexandra.springboot2.model.User;
import se.jensen.alexandra.springboot2.repository.PostRepository;
import se.jensen.alexandra.springboot2.repository.UserRepository;
import se.jensen.alexandra.springboot2.security.MyUserDetails;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * En serviceklass som hanterar allt som har med användare(user) att göra, inklusive deras inlägg(posts)
 */
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final PasswordEncoder passwordEncoder;
    private final PostRepository postRepository;

    public UserService(UserRepository userRepository, UserMapper userMapper, PostMapper postMapper, PasswordEncoder passwordEncoder, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.postMapper = postMapper;
        this.passwordEncoder = passwordEncoder;
        this.postRepository = postRepository;
    }

    /**
     * Metod som hämtar en användare med ett specifikt ID inklusive deras inlägg
     *
     * @param id       - användarens ID
     * @param pageable - information om sidindelning för inlägg
     * @return DTO med användarens information och inlägg
     */
    @Transactional
    public UserWithPostsResponseDTO getUserWithPosts(Long id, Pageable pageable) {
        logger.info("Hämtar användare med id {} och dess inlägg", id);
        User user = userRepository.getUserWithPosts(id)
                .orElseThrow(() -> {
                    logger.warn("Användare med id {} hittades inte", id);
                    return new NoSuchElementException("Ingen användare finns med id: " + id);
                });

        Page<PostResponseDTO> posts = postRepository
                .findByUserId(id, pageable)
                .map(postMapper::toDto);
        return new UserWithPostsResponseDTO(
                userMapper.toDto(user),
                posts
        );
    }

    /**
     * Metod som hämtar alla användare i databasen
     *
     * @return UserResponseDTO - lista med användarinfo
     */
    @Transactional
    public List<UserResponseDTO> getAllUsers() {
        logger.info("Hämtar alla användare");
        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            logger.warn("Inga användare hittades i databasen");
            throw new NoSuchElementException("Inga användare hittades i databasen");
        } else if (users.size() == 1) {
            logger.info("Endast en användare hittades i databasen");
        }

        return users.stream()
                .map(userMapper::toDto).toList();
    }

    /**
     * Metod som hämtar information om just den användare som är inloggad
     *
     * @param userDetails - information om den inloggade användaren
     * @return userMapper.toDto - DTO med användarinfo
     */
    @Transactional
    public UserResponseDTO getCurrentUser(MyUserDetails userDetails) {
        String username = userDetails.getUsername();
        logger.info("Hämtar nuvarande användare: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("Användare {} hittades inte", username);
                    return new NoSuchElementException("Användare hittades inte: " + username);
                });
        return userMapper.toDto(user);
    }

    /**
     * Metod som hämtar en användare via ID
     *
     * @param id - användarens ID
     * @return userMapper.toDto - DTO med användarinfo
     */
    @Transactional
    public UserResponseDTO findUserById(Long id) {
        logger.info("Hämtar användare med id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Användare med id {} hittades inte", id);
                    return new NoSuchElementException("Ingen användare i databasen med id: " + id);
                });
        return userMapper.toDto(user);
    }

    /**
     * Metod som skapar en ny användare
     * Krypterar lösenordet innan det sparas
     * Sparar användaren i databasen
     *
     * @param userDto - information om användaren som ska skapas
     * @return userMapper.toDto(savedUser) - DTO med den skapade användarinfo
     */
    @Transactional
    public UserResponseDTO addUser(UserRequestDTO userDto) {
        logger.info("Lägger till ny användare med användarnamn: {}", userDto.username());
        User user = userMapper.fromDto(userDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        boolean userExists = userRepository.existsByUsernameOrEmail
                (user.getUsername(), user.getEmail());

        if (userExists) {
            logger.warn("Kunde inte lägga till användare. Användarnamn eller e-post finns redan: {}, {}", user.getUsername(), user.getEmail());
            throw new IllegalArgumentException("Användarnamn eller e-post finns redan i databasen");
        }
        User savedUser = userRepository.save(user);
        logger.info("Användare skapad med id: {}", savedUser.getId());
        return userMapper.toDto(savedUser);
    }

    /**
     * Metod som uppdaterar en användare via ID
     * Om lösenordet skickas med krypteras det innan uppdatering
     *
     * @param userDto - Information som ska uppdateras
     * @param id      - ID på den användare som ska uppdateras
     * @return userMapper.toDto(updatedUser) - DTO med den uppdaterade användarinfo
     */
    @Transactional
    public UserResponseDTO updateUser(UserRequestDTO userDto, Long id) {
        logger.info("Uppdaterar användare med id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Kunde inte uppdatera. Användare med id {} hittades inte", id);
                    return new NoSuchElementException("Ingen användare i databasen med id: " + id);
                });
        userMapper.fromDto(user, userDto);

        if (userDto.password() != null && !userDto.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.password()));
        }
        User updatedUser = userRepository.save(user);
        logger.info("Användare med id {} uppdaterad", id);
        return userMapper.toDto(updatedUser);
    }

    /**
     * Metod som tar bort en user via ID
     *
     * @param id - användarens ID som ska tas bort
     */
    @Transactional
    public void deleteUserById(Long id) {
        logger.info("Tar bort användare med id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Kunde inte ta bort. Användare med id {} hittades inte", id);
                    return new NoSuchElementException("Ingen användare i databasen med id: " + id);
                });
        userRepository.delete(user);
        logger.info("Användare med id {} borttagen", id);
    }

    /**
     * Metod som hämtar en användare via användarnamn (username)
     *
     * @param username - Användarens användarnamn
     * @return Optional<User> user - Returnerar användare om det finns, annars tom
     */
    public Optional<User> getUserByUsername(String username) {
        logger.info("Hämtar användare via användarnamn: {}", username);
        Optional<User> user = userRepository.findByUsername(username);
        return user;
    }

    @Transactional
    public UserResponseDTO registerUser(RegisterUserRequestDTO dto) {
        logger.info("Registrerar en ny användare: {}", dto.username());

        boolean userExists = userRepository.existsByUsernameOrEmail(dto.username(), dto.email());

        if (userExists) {
            throw new IllegalArgumentException("Användarnamn eller e-post finns redan");
        }
        User user = new User();
        user.setUsername(dto.username());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setEmail(dto.email());

        //Defaultvärden
        user.setRole("USER");
        user.setBio("");
        user.setDisplayName(dto.username());
        user.setProfileImagePath("");

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}



