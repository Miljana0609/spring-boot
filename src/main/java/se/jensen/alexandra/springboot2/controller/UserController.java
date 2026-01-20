package se.jensen.alexandra.springboot2.controller;

import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import se.jensen.alexandra.springboot2.dto.*;
import se.jensen.alexandra.springboot2.security.MyUserDetails;
import se.jensen.alexandra.springboot2.service.PostService;
import se.jensen.alexandra.springboot2.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final PostService postService;

    public UserController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    //Hämta lista på alla användare
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAll() {
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMe
            (@AuthenticationPrincipal MyUserDetails userDetails) {
        return ResponseEntity.ok(userService.getCurrentUser(userDetails));
    }


    //Hämta en enskild användare
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById
    (@PathVariable Long id) {
        return ResponseEntity.ok().body(userService.findUserById(id));
    }

    //Lägg till en användare
    @PostMapping
    public ResponseEntity<UserResponseDTO> addUser
    (@Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(dto));
    }

    //Uppdatera en användare
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser
    (@PathVariable Long id,
     @Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok().body(userService.updateUser(dto, id));
    }

    //Radera en användare
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser
    (@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //Skapa ett inlägg för en användare
    @PostMapping("/{userId}/posts")
    public ResponseEntity<PostResponseDTO> createPostForUser(
            @PathVariable Long userId,
            @Valid @RequestBody PostRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(userId, request));
    }

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

}
