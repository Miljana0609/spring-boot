package se.jensen.alexandra.springboot2.dto;

public final class UserResponseDTOBuilder {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String displayName;
    private String bio;
    private String profileImagePath;

    private UserResponseDTOBuilder() {
    }

    public static UserResponseDTOBuilder builder() {
        return new UserResponseDTOBuilder();
    }

    public UserResponseDTOBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public UserResponseDTOBuilder username(String username) {
        this.username = username;
        return this;
    }

    public UserResponseDTOBuilder email(String email) {
        this.email = email;
        return this;
    }

    public UserResponseDTOBuilder role(String role) {
        this.role = role;
        return this;
    }

    public UserResponseDTOBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public UserResponseDTOBuilder bio(String bio) {
        this.bio = bio;
        return this;
    }

    public UserResponseDTOBuilder profileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
        return this;
    }

    public UserResponseDTO build() {
        return new UserResponseDTO(
                id,
                username,
                email,
                role,
                displayName,
                bio,
                profileImagePath
        );
    }

    public static UserResponseDTOBuilder from(UserResponseDTO dto) {
        return builder()
                .id(dto.id())
                .username(dto.username())
                .email(dto.email())
                .role(dto.role())
                .displayName(dto.displayName())
                .bio(dto.bio())
                .profileImagePath(dto.profileImagePath());
    }
}
