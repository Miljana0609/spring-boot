package se.jensen.alexandra.springboot2.mapper;

import org.springframework.stereotype.Component;
import se.jensen.alexandra.springboot2.dto.FriendshipResponseDTO;
import se.jensen.alexandra.springboot2.model.Friendship;

import static se.jensen.alexandra.springboot2.dto.FriendshipResponseDTOBuilder.builder;

@Component
public class FriendshipMapper {
    private final UserMapper userMapper;

    public FriendshipMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public FriendshipResponseDTO toDto(Friendship friendship) {
        return builder()
                .id(friendship.getId())
                .status(friendship.getStatus().name())
                .requester(friendship.getRequester() != null
                        ? userMapper.toDto(friendship.getRequester())
                        : null)
                .receiver(friendship.getReceiver() != null
                        ? userMapper.toDto(friendship.getReceiver())
                        : null)
                .build();
    }


}