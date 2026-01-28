// java
package se.jensen.alexandra.springboot2.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.jensen.alexandra.springboot2.dto.FriendshipResponseDTO;
import se.jensen.alexandra.springboot2.dto.UserResponseDTO;
import se.jensen.alexandra.springboot2.mapper.FriendshipMapper;
import se.jensen.alexandra.springboot2.model.Friendship;
import se.jensen.alexandra.springboot2.model.User;
import se.jensen.alexandra.springboot2.repository.FriendshipRepository;
import se.jensen.alexandra.springboot2.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendshipServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private FriendshipRepository friendshipRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FriendshipMapper friendshipMapper;

    private FriendshipService friendshipService;

    private User requester;
    private User receiver;
    private UserResponseDTO requesterDto;
    private UserResponseDTO receiverDto;

    @BeforeEach
    void setUp() {
        friendshipService = new FriendshipService(
                userService, friendshipRepository, userRepository, friendshipMapper);
    }

    @Test
    void sendFriendRequest_success() {
        Long requesterId = 1L;
        Long receiverId = 2L;

        User requester = user(requesterId);
        User receiver = user(receiverId);

        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(friendshipRepository.existsByRequesterIdAndReceiverId(1L, 2L)).thenReturn(false);
        when(friendshipRepository.existsByReceiverIdAndRequesterId(2L, 1L)).thenReturn(false);

        Friendship saved = friendship(10L, requester, receiver, Friendship.Status.PENDING);
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(saved);

        FriendshipResponseDTO dto = new FriendshipResponseDTO(10L, requesterDto, receiverDto, "PENDING");
        when(friendshipMapper.toDto(any(Friendship.class))).thenReturn(dto);

        FriendshipResponseDTO result = friendshipService.sendFriendRequest(1L, 2L);

        assertSame(dto, result);

        ArgumentCaptor<Friendship> captor = ArgumentCaptor.forClass(Friendship.class);
        verify(friendshipRepository).save(captor.capture());

        Friendship captured = captor.getValue();
        assertEquals(Friendship.Status.PENDING, captured.getStatus());
        assertEquals(1L, captured.getRequester().getId());
        assertEquals(2L, captured.getReceiver().getId());
        saved.setId(10L);
    }

    @Test
    void sendFriendRequest_alreadyExists_throws() {
        Long requesterId = 1L;
        Long receiverId = 2L;

        User requester = user(requesterId);
        User receiver = user(receiverId);

        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(friendshipRepository.existsByRequesterIdAndReceiverId(1L, 2L)).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> friendshipService.sendFriendRequest(1L, 2L));

        verify(friendshipRepository, never()).save(any());
    }

    @Test
    void acceptFriendRequest_success() {
        Long requesterId = 1L;
        Long receiverId = 2L;

        User requester = user(requesterId);
        User receiver = user(receiverId);
        
        Friendship f = friendship(5L, requester, receiver, Friendship.Status.PENDING);

        when(friendshipRepository.findById(5L)).thenReturn(Optional.of(f));
        when(friendshipRepository.save(any(Friendship.class))).thenAnswer(inv -> inv.getArgument(0));

        FriendshipResponseDTO dto = new FriendshipResponseDTO(5L, requesterDto, receiverDto, "ACCEPTED");
        when(friendshipMapper.toDto(any(Friendship.class))).thenReturn(dto);

        FriendshipResponseDTO result = friendshipService.acceptFriendRequest(5L, 2L);

        assertSame(dto, result);
        assertEquals(Friendship.Status.ACCEPTED, f.getStatus());
        assertNotNull(f.getUpdatedAt());
        verify(friendshipRepository).save(f);
    }

    @Test
    void getFriendshipsAllRelations_returnsList() {
        Long userId = 1L;
        Friendship f = friendship(42L, user(1L), user(2L), Friendship.Status.ACCEPTED);
        when(friendshipRepository.findByRequesterIdOrReceiverId(userId, userId))
                .thenReturn(Collections.singletonList(f));

        List<Friendship> result = friendshipService.getFriendshipsAllRelations(userId);

        assertEquals(1, result.size());
        assertEquals(f, result.get(0));
    }

    private User user(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private UserResponseDTO userDto(Long id) {
        return new UserResponseDTO(id, "name", "user@example.com", "User", "user", "bio", null);
    }

    private Friendship friendship(Long id, User requester, User receiver, Friendship.Status status) {
        Friendship friendship = new Friendship();
        friendship.setId(id);
        friendship.setRequester(requester);
        friendship.setReceiver(receiver);
        friendship.setStatus(status);
        return friendship;
    }
}

