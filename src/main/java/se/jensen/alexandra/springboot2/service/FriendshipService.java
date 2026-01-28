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
 * En serviceklass som ansvarar för all affärslogik kring vänskapsrelationer.
 * Klassen kommunicerar med databasen via repository-klasser och använder FriendshipMapper
 * för att omvandla mellan entiteter och DTO:er.
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
     * Metod som skickar en vänförfrågan mellan två användare.
     *
     * @param requesterId - ID för användaren som skickar förfrågan
     * @param receiverId  - ID för användaren som tar emot förfrågan
     * @return FriendshipResponseDTO - den skapade vänförfrågan
     */
    public FriendshipResponseDTO sendFriendRequest(Long requesterId, Long receiverId) {
        logger.info("Skickar vänförfrågan från användare {} till användare {}", requesterId, receiverId);
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Avsändaren finns inte."));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Mottagaren finns inte."));

        if (requesterId.equals(receiverId)) {
            logger.warn("Mottager {} hittades inte", receiverId);
            throw new IllegalArgumentException("Användare kan inte skicka vänförfrågan till sig själv.");
        }

        if (friendshipRepository.existsByRequesterIdAndReceiverId(requesterId, receiverId) ||
                friendshipRepository.existsByReceiverIdAndRequesterId(receiverId, requesterId)) {
            logger.warn("Vänskap finns redan mellan användare {} och {}", requesterId, receiverId);
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
     * Metod som accepterar en vänförfrågan.
     *
     * @param friendshipId - ID för vänförfrågan
     * @param userId       - ID för användaren som accepterar
     * @return FriendshipResponseDTO - uppdaterad relation
     */
    @Transactional
    public FriendshipResponseDTO acceptFriendRequest(Long friendshipId, Long userId) {
        logger.info("Accepterar vänförfrågan med ID: {}", friendshipId);
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalArgumentException("Vänförfrågan finns inte."));

        if (friendship.getStatus() != Friendship.Status.PENDING) {
            logger.warn("Vänskapsförfrågan {} är inte väntande (status={})", friendshipId, friendship.getStatus());
            throw new IllegalStateException("Endast väntande vänförfrågningar kan accepteras.");
        }

        if (!friendship.getReceiver().getId().equals(userId)) {
            logger.warn("Användare {} är inte mottagare av vänskapsförfrågan {}", userId, friendshipId);
            throw new IllegalArgumentException("Endast mottagaren kan acceptera vänförfrågan.");
        }

        friendship.setStatus(Friendship.Status.ACCEPTED);
        friendship.setUpdatedAt(LocalDateTime.now());
        friendshipRepository.save(friendship);
        logger.info("Vänförfrågan accepterad framgångsrikt.");

        return friendshipMapper.toDto(friendship);
    }

    /**
     * Metod som avslår en vänförfrågan.
     *
     * @param friendshipId - ID för vänförfrågan
     * @param userId       - ID för användaren som avslår
     * @return FriendshipResponseDTO - uppdaterad relation
     */
    @Transactional
    public FriendshipResponseDTO rejectFriendRequest(Long friendshipId, Long userId) {
        logger.info("Avslår vänförfrågan med ID: {}", friendshipId);
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalArgumentException("Vänförfrågan finns inte."));

        if (friendship.getStatus() != Friendship.Status.PENDING) {
            logger.warn("Status på vänskapsförfrågan {} är ej väntane (status={})", friendshipId, friendship.getStatus());
            throw new IllegalStateException("Endast väntande vänförfrågningar kan avslås.");
        }

        if (!friendship.getReceiver().getId().equals(userId)) {
            logger.warn("Användare {} är inte mottagare av vänskapsförfrågan {}", userId, friendshipId);
            throw new IllegalArgumentException("Endast mottagaren kan avslå vänförfrågan.");
        }

        friendship.setStatus(Friendship.Status.REJECTED);
        friendship.setUpdatedAt(LocalDateTime.now());
        friendshipRepository.save(friendship);
        logger.info("Vänförfrågan avslagen framgångsrikt.");

        return friendshipMapper.toDto(friendship);
    }

    /**
     * Metod som hämtar inkommande vänförfrågningar för en användare.
     *
     * @param userId   - användarens ID
     * @param pageable - pagineringsinställningar
     * @return Page<Friendship> - inkommande förfrågningar
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
     * Metod som hämtar utgående vänförfrågningar för en användare.
     *
     * @param userId   - användarens ID
     * @param pageable - pagineringsinställningar
     * @return Page<Friendship> - skickade förfrågningar
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
     * Metod som hämtar alla accepterade vänner för en användare.
     *
     * @param userId   - användarens ID
     * @param pageable - pagineringsinställningar
     * @return Page<User> - lista med vänner
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
            logger.debug("Hämtar vänner - page start {} >= antal vänner {} för användare {}", start, friends.size(), userId);
            return new PageImpl<>(Collections.emptyList(), pageable, friends.size());
        }

        List<User> pagedFriends = friends.subList(start, end);
        Page<User> result = new PageImpl<>(pagedFriends, pageable, friends.size());
        logger.info("Hämtar vänner och returnerar sida med {} element (totalt {}) för användare {}", pagedFriends.size(), friends.size(), userId);
        return result;
    }

    /**
     * Metod som hämtar alla relationer där användaren är requester eller receiver.
     *
     * @param userId - användarens ID
     * @return lista med relationer
     */
    public List<Friendship> getFriendshipsAllRelations(Long userId) {
        logger.info("Hämtar alla vänskapsrelationer för användare ={}", userId);
        List<Friendship> list = friendshipRepository.findByRequesterIdOrReceiverId(userId, userId);
        int size = list != null ? list.size() : 0;
        logger.debug("Hämtar alla vänskapsrelationer - hittade : {} relationer för användare {}", size, userId);
        return list != null ? list : List.of();
    }

    /**
     * Metod som hämtar vänskapsstatus mellan två användare.
     *
     * @param currentUserId - inloggad användares ID
     * @param profileUserId - profilens användar-ID
     * @return FriendshipStatusResponseDTO - status och relationens metadata
     */
    public FriendshipStatusResponseDTO getStatus(Long currentUserId, Long profileUserId) {
        logger.info("Hämtar status för inloggad användare = {}- profilens användare={}", currentUserId, profileUserId);
        var opt = friendshipRepository.findByUsers(currentUserId, profileUserId);
        FriendshipStatusResponseDTO dto = opt
                .map(f -> new FriendshipStatusResponseDTO(
                        f.getStatus().name(),
                        f.getId(),
                        f.getRequester().getId(),
                        f.getReceiver().getId()
                ))
                .orElse(new FriendshipStatusResponseDTO("NONE", null, null, null));
        logger.debug("Hämtar relationsstatus - resultat för användare {} och {}: {}", currentUserId, profileUserId, dto);
        return dto;
    }
}
