package se.jensen.alexandra.springboot2.controller;

import jakarta.validation.Valid;
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
    @GetMapping("/posts")
    public Page<PostResponseDTO> getPosts(
            @PageableDefault(
                    size = 10,
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

//    @PostMapping("/{userId}")
//    public ResponseEntity<PostResponseDTO> createPost
//            (@PathVariable Long userId,
//             @Valid @RequestBody PostRequestDTO dto) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(userId, dto));
//    }

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
