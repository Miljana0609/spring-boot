package se.jensen.alexandra.springboot2.mapper;

import org.springframework.stereotype.Component;
import se.jensen.alexandra.springboot2.dto.PostRequestDTO;
import se.jensen.alexandra.springboot2.dto.PostResponseDTO;
import se.jensen.alexandra.springboot2.model.Post;

/**
 * En hjälpklass som används för att omvandla mellan Post och PostRequestDTO/PostResponseDTO.
 * Den gör det lättare att skapa, uppdatera och visa inlägg i rätt format.
 */
@Component
public class PostMapper {
    /**
     * Används när man vill skicka data tillbaka till frontend.
     * Tar ett Post-objekt och skapar PostResponseDTO med ID, text och tid.
     *
     * @param post - Post-objekt
     * @return PostResponseDTO - information om ett inlägg
     */
    public PostResponseDTO toDto(Post post) {
        return new PostResponseDTO(
                post.getId(),
                post.getText(),
                post.getCreatedAt());
    }

    /**
     * Används när man skapar ett nytt inlägg.
     * Skapar nytt Post-objekt från PostRequestDTO.
     *
     * @param postDto - Information om ett inlägg.
     * @return Post - det nya inlägget.
     */
    public Post fromDto(PostRequestDTO postDto) {
        Post post = new Post();
        post.setText(postDto.text());
        return post;
    }

    /**
     * Används när man ändrar ett redan existerande inlägg. Uppdaterar endast texten.
     * Används av PostService vid uppdatering av inlägg.
     *
     * @param postDto - Information från frontend om inlägget
     * @param post    - Post-objekt som ska uppdateras
     */
    public void updateEntityFromDto(PostRequestDTO postDto, Post post) {
        post.setText(postDto.text());
    }
}
