package se.jensen.alexandra.springboot2.controller;

import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import se.jensen.alexandra.springboot2.dto.*;
import se.jensen.alexandra.springboot2.model.User;
import se.jensen.alexandra.springboot2.repository.UserRepository;
import se.jensen.alexandra.springboot2.service.PostService;
import se.jensen.alexandra.springboot2.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

/**
 * EN REST-controller som hanterar användare och deras inlägg i applikationen.
 * Använder UserService för allt som har med användare att göra och PostService för inlägg.
 * Varje metod svarar på olika HTTP anrop och skickar data till/från frontend.
 */
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final PostService postService;
    private final UserRepository userRepository;

    public UserController(UserService userService, PostService postService, UserRepository userRepository) {
        this.userService = userService;
        this.postService = postService;
        this.userRepository = userRepository;
    }

    /**
     * Hämtar lista med alla användare. Kräver ADMIN-roll
     *
     * @return - EN lista med alla användare
     */
    //Hämta lista på alla användare
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAll() {
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    /**
     * Hämtar information om den inloggade användaren. Kräver USER-roll.
     *
     * @param authentication - Detaljer om den inloggade användare
     * @return - Information om den inloggade användare
     */
    //@PreAuthorize("hasRole('USER')")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMe
    (Authentication authentication) {
        String username = authentication.getName();
        UserResponseDTO dto = userService.getCurrentUserByUsername(username);
        return ResponseEntity.ok(dto);
    }


    /**
     * Hämtar information om en specifik användare med ID. Kräver ADMIN-roll.
     *
     * @param id - användarens ID
     * @return - Returnerar den användare med angivna ID:t
     */
    //Hämta en enskild användare
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById
    (@PathVariable Long id) {
        return ResponseEntity.ok().body(userService.findUserById(id));
    }

    /**
     * Skapar en ny användare med information från frontend.
     *
     * @param dto -Data för den nya användaren.
     * @return - Returnerar den skapade användaren.
     */
    //Lägg till en användare
    @PostMapping
    public ResponseEntity<UserResponseDTO> addUser
    (@Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(dto));
    }

    /**
     * Uppdaterar en befintlig användare med nytt data
     *
     * @param id  - användarens ID
     * @param dto - Ny information om användaren
     * @return - Returnerar uppdaterade info om användaren.
     */
    //Uppdatera en användare
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser
    (@PathVariable Long id,
     @Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok().body(userService.updateUser(dto, id));
    }

    /**
     * Tar bort en användare från databasen.
     *
     * @param id - Användarens ID.
     * @return - Inget innehåll (HTTP 204)
     */
    //Radera en användare
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser
    (@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Skapar ett nytt inlägg för en specifik användare.
     *
     * @param userId  - Användarens ID
     * @param request - Data för det nya inlägget
     * @return - Det skapade inlägget
     */
    //Skapa ett inlägg för en användare
    @PostMapping("/{userId}/posts")
    public ResponseEntity<PostResponseDTO> createPostForUser(
            @PathVariable Long userId,
            @Valid @RequestBody PostRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(userId, request));
    }

    /**
     * Hämtar en användare och alla deras inlägg med paginering.
     *
     * @param id       - Användarens ID
     * @param pageable - Paginering och sortering av inlägg
     * @return UserWithPostsResponseDTO - som innehåller både användarinfo och en sida med inlägg.
     */
    //Hämta alla inlägg från samt info om användare
    @GetMapping("/{id}/with-posts")
    public UserWithPostsResponseDTO getUserWithPosts(
            @PathVariable Long id,
            @ParameterObject @PageableDefault(
                    size = 5,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        int size = pageable.getPageSize() <= 0 ? 5 : Math.min(pageable.getPageSize(), 5);
        Pageable fixed = PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
        return userService.getUserWithPosts(id, fixed);
    }

    /**
     * Registrerar en ny användare
     *
     * @param dto - data som behövs
     * @return UserResponseDTO - information om den registrerade användaren
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterUserRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(dto));
    }


    /**
     * Uppdaterar den inloggade användarens profilinformation.
     * Endast den autentiserade användaren kan uppdatera sin egen profil.
     *
     * @param authentication - inloggad användarens säkerhetsinformation
     * @param dto            - objekt som innehåller uppdaterad profilinformation
     * @return UserResponseDTO - uppdaterad användarprofil
     */
    //@PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateProfile(
            @RequestBody UpdateProfileDTO dto,
            Authentication authentication) {

        String username = authentication.getName();
        User authUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Användare hittades ej med användanamn:" + username));

        UserResponseDTO updatedUser = userService.updateProfile(authUser.getId(), dto);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/profile-image")
    public ResponseEntity<Void> uploadProfileImage(
            @RequestParam("image") MultipartFile file,
            Authentication authentication
    ) {
        userService.saveProfileImage(file, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/profile-image")
    public ResponseEntity<Resource> getProfileImage(
            @PathVariable String username,
            Authentication authentication
    ) {
        Resource resource = userService.getProfileImage(username, authentication);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic())
                .body(resource);
    }

}
