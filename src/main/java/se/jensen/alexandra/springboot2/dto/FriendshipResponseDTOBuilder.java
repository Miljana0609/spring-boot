package se.jensen.alexandra.springboot2.dto;

/**
 * En builder-klass som används för att skapa instanser av FriendshipResponseDTO.
 * Tillåter stegvis uppbyggnad av DTO:n för ökad läsbarhet och flexibilitet.
 */

public final class FriendshipResponseDTOBuilder {
    private Long id;
    private UserResponseDTO requester;
    private UserResponseDTO receiver;
    private String status;

    private FriendshipResponseDTOBuilder() {
    }

    public static FriendshipResponseDTOBuilder builder() {
        return new FriendshipResponseDTOBuilder();
    }

    public FriendshipResponseDTOBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public FriendshipResponseDTOBuilder requester(UserResponseDTO requester) {
        this.requester = requester;
        return this;
    }

    public FriendshipResponseDTOBuilder receiver(UserResponseDTO receiver) {
        this.receiver = receiver;
        return this;
    }

    public FriendshipResponseDTOBuilder status(String status) {
        this.status = status;
        return this;
    }

    public FriendshipResponseDTO build() {
        return new FriendshipResponseDTO(
                id,
                requester,
                receiver,
                status
        );
    }

    public static FriendshipResponseDTOBuilder from(FriendshipResponseDTO dto) {
        return builder()
                .id(dto.id())
                .requester(dto.requester())
                .receiver(dto.receiver())
                .status(dto.status());
    }
}
