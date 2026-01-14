package se.jensen.alexandra.springboot2.controller;

import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.jensen.alexandra.springboot2.dto.PostRequestDTO;
import se.jensen.alexandra.springboot2.dto.PostResponseDTO;
import se.jensen.alexandra.springboot2.service.PostService;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    //Finns redan i UserController/UserService
    @GetMapping
    public Page<PostResponseDTO> getPosts(
            @ParameterObject
            @PageableDefault(
                    //page = 0, 0 är default och behöver inte anges
                    size = 5,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        return postService.getAllPosts(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDTO> getPostById
            (@PathVariable Long id) {
        return ResponseEntity.ok(postService.findPostById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDTO> updatePost
            (@PathVariable Long id,
             @Valid @RequestBody PostRequestDTO dto) {
        return ResponseEntity.ok(postService.updatePost(dto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost
            (@PathVariable Long id) {
        postService.deletePostById(id);
        return ResponseEntity.noContent().build();
    }
}
