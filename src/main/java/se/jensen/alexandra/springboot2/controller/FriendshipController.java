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

/**
 * Controller för alla vänskapsrelaterade operationer.
 * Hanterar vänförfrågningar, status, accepterade relationer och listning av vänner.
 * Alla endpoints ligger under /friendships.
 */
@RestController
@RequestMapping("/friendships")
public class FriendshipController {
    private final FriendshipService friendshipService;
    private final FriendshipMapper friendshipMapper;
    private final UserMapper userMapper;
    private final UserService userService;

    /**
     * Skapar en ny instans av FriendshipController.
     *
     * @param friendshipService service för vänskapslogik
     * @param friendshipMapper  mapper för att konvertera Friendship → DTO
     * @param userMapper        mapper för att konvertera User → DTO
     * @param userService       service för användarhantering
     */
    public FriendshipController(FriendshipService friendshipService, FriendshipMapper friendshipMapper, UserMapper userMapper, UserService userService) {
        this.friendshipService = friendshipService;
        this.friendshipMapper = friendshipMapper;
        this.userMapper = userMapper;
        this.userService = userService;
    }

    /**
     * Hämtar vänskapsstatus mellan den inloggade användaren och en annan användare.
     *
     * @param userId         ID för användaren som ska jämföras med den inloggade
     * @param authentication autentiseringsobjekt som innehåller inloggad användares info
     * @return DTO som beskriver relationens status (NONE, PENDING, ACCEPTED)
     */
    @GetMapping("/status")
    public FriendshipStatusResponseDTO getStatus(
            @RequestParam Long userId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        UserResponseDTO currentUser = userService.getCurrentUserByUsername(username);
        return friendshipService.getStatus(currentUser.id(), userId);
    }

    /**
     * Skickar en vänförfrågan från en användare till en annan.
     *
     * @param dto innehåller requesterId och receiverId
     * @return skapad vänförfrågan som DTO
     */
    @PostMapping
    public ResponseEntity<FriendshipResponseDTO> createFriendship
    (@Valid @RequestBody FriendshipRequestDTO dto) {

        FriendshipResponseDTO response = friendshipService.sendFriendRequest(dto.requesterId(), dto.receiverId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Hämtar alla vänskapsrelationer (både skickade och mottagna)
     * för en specifik användare.
     *
     * @param id användarens ID
     * @return lista av alla relationer som DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<List<FriendshipResponseDTO>> getFriendshipsAllRelations(
            @PathVariable Long id) {

        List<Friendship> friendships = friendshipService.getFriendshipsAllRelations(id);

        List<FriendshipResponseDTO> dtos = friendships.stream()
                .map(friendshipMapper::toDto)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    /**
     * Accepterar en vänförfrågan.
     *
     * @param id     vänförfrågans ID
     * @param userId ID för användaren som accepterar
     * @return uppdaterad vänskapsrelation som DTO
     */
    @PutMapping("/{id}/accept")
    public ResponseEntity<FriendshipResponseDTO> acceptFriendship(
            @PathVariable Long id,
            @RequestParam Long userId) {

        FriendshipResponseDTO accept = friendshipService.acceptFriendRequest(id, userId);

        return ResponseEntity.ok(accept);
    }

    /**
     * Avvisar en vänförfrågan.
     *
     * @param id     vänförfrågans ID
     * @param userId ID för användaren som avvisar
     * @return uppdaterad vänskapsrelation som DTO
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<FriendshipResponseDTO> rejectFriendship(
            @PathVariable Long id,
            @RequestParam Long userId) {

        FriendshipResponseDTO reject = friendshipService.rejectFriendRequest(id, userId);

        return ResponseEntity.ok(reject);
    }

    /**
     * Hämtar en lista över en användares vänner.
     *
     * @param id       användarens ID
     * @param pageable pagineringsinställningar
     * @return lista av användare som DTO
     */
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

    /**
     * Hämtar alla inkommande vänförfrågningar för en användare.
     *
     * @param id       användarens ID
     * @param pageable pagineringsinställningar
     * @return lista av vänförfrågningar som DTO
     */
    @GetMapping("/users/{id}/requests")
    public ResponseEntity<List<FriendshipResponseDTO>> getReceivedRequests(
            @PathVariable Long id,
            @ParameterObject @PageableDefault(
                    size = 5,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC)
            Pageable pageable) {

        var requests = friendshipService.getIncomingFriendRequests(id, pageable);

        List<FriendshipResponseDTO> dtos = requests.stream()
                .map(friendshipMapper::toDto)
                .toList();

        return ResponseEntity.ok(dtos);
    }
}
