package se.jensen.alexandra.springboot2.mapper;

import org.springframework.stereotype.Component;
import se.jensen.alexandra.springboot2.dto.PostRequestDTO;
import se.jensen.alexandra.springboot2.dto.PostResponseDTO;
import se.jensen.alexandra.springboot2.model.Post;

@Component
public class PostMapper {
    public PostResponseDTO toDto(Post post) {
        return new PostResponseDTO(
                post.getId(),
                post.getText(),
                post.getCreatedAt());
    }

    public Post fromDto(PostRequestDTO userDto) {
        Post post = new Post();
        post.setText(userDto.text());
        return post;
    }

    public void updateEntityFromDto(PostRequestDTO userDto, Post post) {
        post.setText(userDto.text());
    }
}
