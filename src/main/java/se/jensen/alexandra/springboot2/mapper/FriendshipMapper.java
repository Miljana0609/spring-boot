package se.jensen.alexandra.springboot2.mapper;

import org.springframework.stereotype.Component;
import se.jensen.alexandra.springboot2.dto.FriendshipResponseDTO;
import se.jensen.alexandra.springboot2.model.Friendship;

import static se.jensen.alexandra.springboot2.dto.FriendshipResponseDTOBuilder.builder;

/**
 * Mapper som konverterar Friendship‑entiteter till FriendshipResponseDTO.
 * Används för att skicka vänskapsdata till frontend.
 */
@Component
public class FriendshipMapper {
    private final UserMapper userMapper;

    public FriendshipMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * Konverterar en Friendship‑entitet till ett FriendshipResponseDTO.
     *
     * @param friendship - vänskapsrelation som ska konverteras
     * @return ett DTO med relationens data
     */
    public FriendshipResponseDTO toDto(Friendship friendship) {
        return builder()
                .id(friendship.getId())
                .status(friendship.getStatus().name())
                .requester(userMapper.toDto(friendship.getRequester()))
                .receiver(userMapper.toDto(friendship.getReceiver()))
                .build();
    }
}