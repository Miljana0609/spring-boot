package se.jensen.alexandra.springboot2.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.jensen.alexandra.springboot2.dto.FriendshipResponseDTO;
import se.jensen.alexandra.springboot2.dto.FriendshipStatusResponseDTO;
import se.jensen.alexandra.springboot2.mapper.FriendshipMapper;
import se.jensen.alexandra.springboot2.model.Friendship;
import se.jensen.alexandra.springboot2.model.User;
import se.jensen.alexandra.springboot2.repository.FriendshipRepository;
import se.jensen.alexandra.springboot2.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Serviceklass som hanterar all affärslogik kopplad till vänskapsrelationer.
 * Ansvarar för att skicka, acceptera och avslå vänförfrågningar samt
 * hämta information om vänstatus och vänlistor.
 */
@Service
public class FriendshipService {
    private static final Logger logger = LoggerFactory.getLogger(FriendshipService.class);
    private final UserService userService;
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final FriendshipMapper friendshipMapper;

    public FriendshipService(UserService userService, FriendshipRepository friendshipRepository, UserRepository userRepository, FriendshipMapper friendshipMapper) {
        this.userService = userService;
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
        this.friendshipMapper = friendshipMapper;
    }

    /**
     * Skickar en vänförfrågan från en användare till en annan.
     * Kontrollerar att båda användarna existerar, att användaren
     * inte skickar förfrågan till sig själv samt att ingen
     * befintlig relation redan finns.
     *
     * @param requesterId - ID på användaren som skickar vänförfrågan
     * @param receiverId  - ID på användaren som tar emot vänförfrågan
     * @return - skapad vänförfrågan som DTO
     */
    public FriendshipResponseDTO sendFriendRequest(Long requesterId, Long receiverId) {
        logger.info("Skickar vänförfrågan från användare {} till användare {}", requesterId, receiverId);
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Avsändaren finns inte."));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Mottagaren finns inte."));

        if (requesterId.equals(receiverId)) {
            throw new IllegalArgumentException("Användare kan inte skicka vänförfrågan till sig själv.");
        }

        if (friendshipRepository.existsByRequesterIdAndReceiverId(requesterId, receiverId) ||
                friendshipRepository.existsByReceiverIdAndRequesterId(receiverId, requesterId)) {
            throw new IllegalStateException("En vänförfrågan finns redan mellan dessa användare.");
        }

        Friendship friendship = new Friendship();
        friendship.setStatus(Friendship.Status.PENDING);
        friendship.setCreatedAt(LocalDateTime.now());
        friendship.setRequester(requester);
        friendship.setReceiver(receiver);
        friendshipRepository.save(friendship);
        logger.info("Vänförfrågan skickad framgångsrikt.");
        return friendshipMapper.toDto(friendship);
    }

    /**
     * Accepterar en väntande vänförfrågan.
     * Endast mottagaren av förfrågan har rätt att acceptera den.
     *
     * @param friendshipId - ID på vänförfrågan
     * @param userId       - ID på användaren som accepterar
     * @return - uppdaterad vänrelation som DTO
     */
    @Transactional
    public FriendshipResponseDTO acceptFriendRequest(Long friendshipId, Long userId) {
        logger.info("Accepterar vänförfrågan med ID: {}", friendshipId);
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalArgumentException("Vänförfrågan finns inte."));

        if (friendship.getStatus() != Friendship.Status.PENDING) {
            throw new IllegalStateException("Endast väntande vänförfrågningar kan accepteras.");
        }

        if (!friendship.getReceiver().getId().equals(userId)) {
            throw new IllegalArgumentException("Endast mottagaren kan acceptera vänförfrågan.");
        }

        friendship.setStatus(Friendship.Status.ACCEPTED);
        friendship.setUpdatedAt(LocalDateTime.now());
        friendshipRepository.save(friendship);
        logger.info("Vänförfrågan accepterad framgångsrikt.");

        return friendshipMapper.toDto(friendship);
    }

    /**
     * Avslår en väntande vänförfrågan.
     * Endast mottagaren av förfrågan har rätt att avslå den.
     *
     * @param friendshipId - ID på vänförfrågan
     * @param userId       - ID på användaren som avslår
     * @return - uppdaterad vänrelation som DTO
     */
    @Transactional
    public FriendshipResponseDTO rejectFriendRequest(Long friendshipId, Long userId) {
        logger.info("Avslår vänförfrågan med ID: {}", friendshipId);
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalArgumentException("Vänförfrågan finns inte."));

        if (friendship.getStatus() != Friendship.Status.PENDING) {
            throw new IllegalStateException("Endast väntande vänförfrågningar kan avslås.");
        }

        if (!friendship.getReceiver().getId().equals(userId)) {
            throw new IllegalArgumentException("Endast mottagaren kan avslå vänförfrågan.");
        }

        friendship.setStatus(Friendship.Status.REJECTED);
        friendship.setUpdatedAt(LocalDateTime.now());
        friendshipRepository.save(friendship);
        logger.info("Vänförfrågan avslagen framgångsrikt.");

        return friendshipMapper.toDto(friendship);
    }

    /**
     * Hämtar inkommande vänförfrågningar för en användare.
     *
     * @param userId   - användarens ID
     * @param pageable - sidindelning och sortering
     * @return - sida med inkommande vänförfrågningar
     */
    public Page<Friendship> getIncomingFriendRequests(Long userId, Pageable pageable) {
        logger.info("Hämtar inkommande vänförfrågningar för användare {} med pageable: {}", userId, pageable);
        return friendshipRepository.findByReceiverIdAndStatus(
                userId,
                Friendship.Status.PENDING,
                pageable
        );
    }

    /**
     * Hämtar utgående vänförfrågningar för en användare.
     *
     * @param userId   - användarens ID
     * @param pageable - sidindelning och sortering
     * @return - sida med utgående vänförfrågningar
     */
    public Page<Friendship> getOutgoingFriendRequests(Long userId, Pageable pageable) {
        logger.info("Hämtar utgående vänförfrågningar för användare {} med pageable: {}", userId, pageable);
        return friendshipRepository.findByRequesterIdAndStatus(
                userId,
                Friendship.Status.PENDING,
                pageable
        );
    }

    /**
     * Hämtar en paginerad lista med användarens vänner.
     * En vän definieras som en accepterad relation där
     * användaren är antingen mottagare eller avsändare.
     *
     * @param userId   - användarens ID
     * @param pageable - sidindelning och sortering
     * @return - sida med användarens vänner
     */
    public Page<User> getFriends(Long userId, Pageable pageable) {
        logger.info("Hämtar vänner för användare {} med pageable: {}", userId, pageable);
        List<Friendship> requested = friendshipRepository.findByRequesterIdAndStatus(
                userId,
                Friendship.Status.ACCEPTED
        );
        List<Friendship> received = friendshipRepository.findByReceiverIdAndStatus(
                userId,
                Friendship.Status.ACCEPTED
        );
        List<Friendship> all = new ArrayList<>();
        all.addAll(requested);
        all.addAll(received);

        List<User> friends = new ArrayList<>();
        for (Friendship friendship : all) {
            if (friendship.getRequester().getId().equals(userId)) {
                friends.add(friendship.getReceiver());
            } else {
                friends.add(friendship.getRequester());
            }
        }
        friends = friends.stream().distinct().toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), friends.size());

        if (start >= friends.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, friends.size());
        }

        List<User> pagedFriends = friends.subList(start, end);

        return new PageImpl<>(pagedFriends, pageable, friends.size());
    }

    /**
     * Hämtar alla vänskapsrelationer (oavsett status)
     * där användaren är antingen avsändare eller mottagare.
     *
     * @param userId - användarens ID
     * @return - lista med alla relaterade vänskapsrelationer
     */
    public List<Friendship> getFriendshipsAllRelations(Long userId) {
        List<Friendship> list = friendshipRepository.findByRequesterIdOrReceiverId(userId, userId);
        return list != null ? list : List.of();
    }

    /**
     * Hämtar vänskapsstatus mellan två användare.
     * Returnerar status samt relevant metadata om en relation finns,
     * annars status NONE.
     *
     * @param currentUserId - inloggad användares ID
     * @param profileUserId - användarens ID vars profil visas
     * @return - DTO med vänskapsstatus
     */
    public FriendshipStatusResponseDTO getStatus(Long currentUserId, Long profileUserId) {
        return friendshipRepository
                .findByUsers(currentUserId, profileUserId)
                .map(f -> new FriendshipStatusResponseDTO(
                        f.getStatus().name(),
                        f.getId(),
                        f.getRequester().getId(),
                        f.getReceiver().getId()
                ))
                .orElse(new FriendshipStatusResponseDTO("NONE", null, null, null));
    }
}
