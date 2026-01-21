package se.jensen.alexandra.springboot2.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.jensen.alexandra.springboot2.dto.PostResponseDTO;
import se.jensen.alexandra.springboot2.dto.UserRequestDTO;
import se.jensen.alexandra.springboot2.dto.UserResponseDTO;
import se.jensen.alexandra.springboot2.dto.UserWithPostsResponseDTO;
import se.jensen.alexandra.springboot2.mapper.PostMapper;
import se.jensen.alexandra.springboot2.mapper.UserMapper;
import se.jensen.alexandra.springboot2.model.User;
import se.jensen.alexandra.springboot2.repository.PostRepository;
import se.jensen.alexandra.springboot2.repository.UserRepository;
import se.jensen.alexandra.springboot2.security.MyUserDetails;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final PasswordEncoder passwordEncoder;
    private final PostRepository postRepository;

    //en liten kommentar bara.. uppdaterar igen då
    public UserService(UserRepository userRepository, UserMapper userMapper, PostMapper postMapper, PasswordEncoder passwordEncoder, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.postMapper = postMapper;
        this.passwordEncoder = passwordEncoder;
        this.postRepository = postRepository;
    }

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

    public Optional<User> getUserByUsername(String username) {
        logger.info("Hämtar användare via användarnamn: {}", username);
        Optional<User> user = userRepository.findByUsername(username);
        return user;
    }
}



