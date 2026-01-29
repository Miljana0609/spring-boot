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
 * En REST-kontroller som hanterar alla anrop relaterade till vänskapsrelationer,
 * såsom att skicka vänförfrågningar, acceptera eller avvisa dem samt
 * hämta vänlistor och status mellan användare.
 */
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

    /**
     * Hämtar vänskapsstatus mellan den inloggade användaren och en annan användare.
     * Statusen kan exempelvis vara: ingen relation, skickad förfrågan,
     * mottagen förfrågan eller vänner.
     *
     * @param userId         - ID på användaren som statusen ska kontrolleras mot
     * @param authentication - inloggad användare
     * @return - ett DTO-objekt som beskriver vänskapsstatusen
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
     * Skapar och skickar en ny vänförfrågan mellan två användare.
     *
     * @param dto - information om avsändare och mottagare av vänförfrågan
     * @return - den skapade vänskapsrelationen
     */
    @PostMapping
    public ResponseEntity<FriendshipResponseDTO> createFriendship
    (@Valid @RequestBody FriendshipRequestDTO dto) {

        FriendshipResponseDTO response = friendshipService.sendFriendRequest(dto.requesterId(), dto.receiverId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Hämtar alla vänskapsrelationer för en användare,
     * oavsett om relationen är accepterad, väntande eller avslagen.
     *
     * @param id - användarens ID
     * @return - en lista med alla vänskapsrelationer
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
     * Accepterar en inkommande vänförfrågan.
     *
     * @param id     - ID på vänskapsförfrågan
     * @param userId - ID på användaren som accepterar förfrågan
     * @return - den uppdaterade vänskapsrelationen
     */
    @PutMapping("/{id}/accept")
    public ResponseEntity<FriendshipResponseDTO> acceptFriendship(
            @PathVariable Long id,
            @RequestParam Long userId) {

        FriendshipResponseDTO accept = friendshipService.acceptFriendRequest(id, userId);

        return ResponseEntity.ok(accept);
    }

    /**
     * Avvisar en inkommande vänförfrågan.
     *
     * @param id     - ID på vänskapsförfrågan
     * @param userId - ID på användaren som avvisar förfrågan
     * @return - den uppdaterade vänskapsrelationen
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<FriendshipResponseDTO> rejectFriendship(
            @PathVariable Long id,
            @RequestParam Long userId) {

        FriendshipResponseDTO reject = friendshipService.rejectFriendRequest(id, userId);

        return ResponseEntity.ok(reject);
    }

    /**
     * Hämtar en paginerad lista med alla accepterade vänner
     * för en specifik användare.
     *
     * @param id       - användarens ID
     * @param pageable - information om sida, storlek och sortering
     * @return - en lista med användare som är vänner
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
     * Hämtar en paginerad lista med inkommande vänförfrågningar
     * för en specifik användare.
     *
     * @param id       - användarens ID
     * @param pageable - information om sida, storlek och sortering
     * @return - en lista med mottagna vänförfrågningar
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
