package se.jensen.alexandra.springboot2.controller;

import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import se.jensen.alexandra.springboot2.dto.PostRequestDTO;
import se.jensen.alexandra.springboot2.dto.PostResponseDTO;
import se.jensen.alexandra.springboot2.service.PostService;

/**
 * En REST-controller som hanterar inlägg (posts) i applikationen.
 * Den använder PostService för att göra alla operationer mot databasen.
 *
 * @RestController gör att klassen kan ta emot HTTP-anrop och skicka JSON-svar.
 * @RequestMapping("/posts") gör att alla endpoints i den här controllern börjar med /posts
 */
@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * Hämtar en sida med inlägg, max 5 i taget.
     * Sorterar inlägg efter createdAt i fallande ordning (nyast först)
     *
     * @param pageable - sidinställningar för paginering
     * @return Page<PostResponseDTO> - som innehåller inläggens data.
     */
    //Finns redan i UserController/UserService
    @GetMapping
    public Page<PostResponseDTO> getPosts(
            @ParameterObject @PageableDefault(
                    size = 5,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC)
            Pageable pageable,
            Authentication authentication
    ) {
        int size = pageable.getPageSize() <= 0 ? 5 : Math.min(pageable.getPageSize(), 5);
        Pageable fixed = PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
        return postService.getAllPosts(fixed, authentication);
    }

    /**
     * Hämtar en specifik inlägg med det angivna ID:t.
     *
     * @param id - Inläggets specifika ID
     * @return PostResponseDTO - med status 200 OK.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDTO> getPostById
    (@PathVariable Long id) {
        return ResponseEntity.ok(postService.findPostById(id));
    }

    /**
     * Uppdaterar det existerande inlägget med angivna ID:t.
     *
     * @param id  - inläggets ID
     * @param dto - Ny data som ska uppdateras
     * @return PostResponseDTO - den uppdaterade inlägget med status 200 OK.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDTO> updatePost
    (@PathVariable Long id,
     @Valid @RequestBody PostRequestDTO dto) {
        return ResponseEntity.ok(postService.updatePost(dto, id));
    }

    /**
     * Tar bort ett inlägg med det angivna ID:t från databasen.
     *
     * @param id - inläggets ID
     * @return - 204 No Content (inget innehåll)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost
    (@PathVariable Long id) {
        postService.deletePostById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Metod som gör att man kan gilla eller ta bort gilla-markering på ett inlägg.
     *
     * @param id             - ID för det inlägg man vill gilla.
     * @param authentication - Information om vem det är som klickar på gilla-knappen.
     * @return - Svarar med 200 OK när det är klart.
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likePost(@PathVariable Long id, Authentication authentication) {
        postService.toggleLike(id, authentication.getName());
        return ResponseEntity.ok().build();
    }
}
