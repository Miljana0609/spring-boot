package se.jensen.alexandra.springboot2.service;

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
import se.jensen.alexandra.springboot2.repository.UserRepository;
import se.jensen.alexandra.springboot2.security.MyUserDetails;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final PasswordEncoder passwordEncoder;

    //en liten kommentar bara.
    public UserService(UserRepository userRepository, UserMapper userMapper, PostMapper postMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.postMapper = postMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserWithPostsResponseDTO getUserWithPosts(Long id) {
        User user = userRepository.getUserWithPosts(id)
                .orElseThrow(() -> new NoSuchElementException("Ingen användare finns med id: " + id));

        List<PostResponseDTO> posts = user.getPosts()
                .stream()
                .map(postMapper::toDto)
                .toList();
        return new UserWithPostsResponseDTO(userMapper.toDto(user), posts);
    }

    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toDto).toList();
    }

    public UserResponseDTO getCurrentUser(MyUserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Användare hittades inte: " + username));
        return userMapper.toDto(user);
    }

    public UserResponseDTO findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ingen användare i databasen med id: " + id));
        return userMapper.toDto(user);
    }

    @Transactional
    public UserResponseDTO addUser(UserRequestDTO userDto) {
        User user = userMapper.fromDto(userDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        boolean userExists = userRepository.existsByUsernameOrEmail
                (user.getUsername(), user.getEmail());

        if (userExists) {
            throw new IllegalArgumentException("Användarnamn eller e-post finns redan i databasen");
        }
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Transactional
    public UserResponseDTO updateUser(UserRequestDTO userDto, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ingen användare i databasen med id: " + id));
        userMapper.fromDto(user, userDto);

        if (userDto.password() != null && !userDto.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.password()));
        }
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ingen användare i databasen med id: " + id));
        userRepository.delete(user);
    }

    public Optional<User> getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user;
    }
}



