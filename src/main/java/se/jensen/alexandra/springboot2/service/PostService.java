package se.jensen.alexandra.springboot2.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import se.jensen.alexandra.springboot2.dto.PostRequestDTO;
import se.jensen.alexandra.springboot2.dto.PostResponseDTO;
import se.jensen.alexandra.springboot2.mapper.PostMapper;
import se.jensen.alexandra.springboot2.model.Post;
import se.jensen.alexandra.springboot2.model.User;
import se.jensen.alexandra.springboot2.repository.PostRepository;
import se.jensen.alexandra.springboot2.repository.UserRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * En serviceklass som ansvarar för all affärslogik kring inlägg (Posts)
 * Klassen kommunicerar med databasen via repository-klasser och använder PostMapper för att omvandla mellan entiteter och DTO:er
 */
@Service
public class PostService {
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;

    public PostService(PostRepository postRepository, UserRepository userRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postMapper = postMapper;
    }

    /**
     * Metod som hämtar alla inlägg från databasen med stöd för paginering
     *
     * @param pageable
     * @return Page<PostResponseDTO> som innehåller inläggens ID, text och datum när det skapades
     */
    //Finns redan i UserController/UserService
    public Page<PostResponseDTO> getAllPosts(Pageable pageable) {
        logger.info("Hämtar alla inlägg med pageable: {}", pageable);
        return postRepository.findAll(pageable)
                .map(post -> new PostResponseDTO(
                        post.getId(),
                        post.getText(),
                        post.getCreatedAt()
                ));
    }

    /**
     * Metod som hämtar ett specifikt inlägg baserat på dess ID
     *
     * @param id - inläggens ID
     * @return PostResponseDTO - om inlägget finns, annars kastas ett NoSuchElementException
     */
    public PostResponseDTO findPostById(Long id) {
        logger.info("Hämtar inlägg med id: {}", id);
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent()) {
            Post realPost = post.get();
            return postMapper.toDto(realPost);
        } else {
            logger.warn("Inlägg med id {} hittades inte", id);
            throw new NoSuchElementException("Inget inlägg i databasen med id: " + id);
        }
    }

    /**
     * Metod som skapar ett nytt inlägg kopplat till en specifik användare
     *
     * @param userId  - Användarens ID
     * @param postDto - Data som används för att uppdatera inlägget
     * @return PostResponseDTO - Inlägg skapas med ID, text och datum när det skapades
     */
    public PostResponseDTO createPost(Long userId, PostRequestDTO postDto) {
        logger.info("Skapar nytt inlägg för användare med id: {}", userId);
        Post post = new Post();
        post.setText(postDto.text());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Kunde inte skapa inlägg. Användare med id {} finns ej", userId);
                    return new NoSuchElementException("Användare finns ej med userId: " + userId);
                });
        post.setUser(user);
        Post savedPost = postRepository.save(post);
        logger.info("Inlägg skapat med id: {}", savedPost.getId());
        return new PostResponseDTO(savedPost.getId(), savedPost.getText(), savedPost.getCreatedAt());
    }

    /**
     * Metod som uppdaterar ett inlägg baserat på ID
     * Om inlägget finns så uppdateras dess innehåll, annars kastas ett undantag
     *
     * @param postDto - Data som används för att uppdatera inlägget
     * @param id      - Inläggets ID
     * @return PostResponseDTO - Uppdaterad inlägg med text och tid
     */
    public PostResponseDTO updatePost(PostRequestDTO postDto, Long id) {
        logger.info("Uppdaterar inlägg med id: {}", id);
        Optional<Post> existing = postRepository.findById(id);
        if (existing.isPresent()) {
            Post post = existing.get();
            postMapper.updateEntityFromDto(postDto, post);
            Post updatedPost = postRepository.save(post);
            logger.info("Inlägg med id {} uppdaterat", id);
            return postMapper.toDto(updatedPost);
        } else {
            logger.warn("Kunde inte uppdatera. Inlägg med id {} hittades inte", id);
            throw new NoSuchElementException("Inget inlägg i databasen med id: " + id);
        }
    }

    /**
     * Metod som tar bort ett inlägg med angivet ID
     * Om inlägget inte finns med angivet ID så kastas ett undantag
     *
     * @param id - Inläggens ID
     */
    public void deletePostById(Long id) {
        logger.info("Tar bort inlägg med id: {}", id);
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent()) {
            postRepository.deleteById(id);
            logger.info("Inlägg med id {} borttaget", id);
        } else {
            logger.warn("Kunde inte ta bort. Inlägg med id {} hittades inte", id);
            throw new NoSuchElementException("Inget inlägg i databasen med id: " + id);
        }
    }
}
