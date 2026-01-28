package se.jensen.alexandra.springboot2.controller;

import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import se.jensen.alexandra.springboot2.dto.FriendshipRequestDTO;
import se.jensen.alexandra.springboot2.dto.FriendshipResponseDTO;
import se.jensen.alexandra.springboot2.dto.FriendshipStatusResponseDTO;
import se.jensen.alexandra.springboot2.dto.UserResponseDTO;
import se.jensen.alexandra.springboot2.mapper.FriendshipMapper;
import se.jensen.alexandra.springboot2.mapper.UserMapper;
import se.jensen.alexandra.springboot2.model.Friendship;
import se.jensen.alexandra.springboot2.service.FriendshipService;
import se.jensen.alexandra.springboot2.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/friendships")
public class FriendshipController {
    private final FriendshipService friendshipService;
    private final FriendshipMapper friendshipMapper;
    private final UserMapper userMapper;
    private final UserService userService;

    public FriendshipController(FriendshipService friendshipService, FriendshipMapper friendshipMapper, UserMapper userMapper, UserService userService) {
        this.friendshipService = friendshipService;
        this.friendshipMapper = friendshipMapper;
        this.userMapper = userMapper;
        this.userService = userService;
    }

    @GetMapping("/status")
    public FriendshipStatusResponseDTO getStatus(
            @RequestParam Long userId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        UserResponseDTO currentUser = userService.getCurrentUserByUsername(username);
        return friendshipService.getStatus(currentUser.id(), userId);
    }

    @PostMapping
    public ResponseEntity<FriendshipResponseDTO> createFriendship
            (@Valid @RequestBody FriendshipRequestDTO dto) {

        FriendshipResponseDTO response = friendshipService.sendFriendRequest(dto.requesterId(), dto.receiverId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<FriendshipResponseDTO>> getFriendshipsAllRelations(
            @PathVariable Long id) {

        List<Friendship> friendships = friendshipService.getFriendshipsAllRelations(id);

        List<FriendshipResponseDTO> dtos = friendships.stream()
                .map(friendshipMapper::toDto)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<FriendshipResponseDTO> acceptFriendship(
            @PathVariable Long id,
            @RequestParam Long userId) {

        FriendshipResponseDTO accept = friendshipService.acceptFriendRequest(id, userId);

        return ResponseEntity.ok(accept);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<FriendshipResponseDTO> rejectFriendship(
            @PathVariable Long id,
            @RequestParam Long userId) {

        FriendshipResponseDTO reject = friendshipService.rejectFriendRequest(id, userId);

        return ResponseEntity.ok(reject);
    }

    @GetMapping("/users/{id}/friends")
    public ResponseEntity<List<UserResponseDTO>> getFriendsByUserId(
            @PathVariable Long id,
            @ParameterObject @PageableDefault(
                    size = 5,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC)
            Pageable pageable) {

        var page = friendshipService.getFriends(id, pageable);

        var dtos = page.stream()
                .map(userMapper::toDto)
                .toList();

        return ResponseEntity.ok(dtos);
    }
}
