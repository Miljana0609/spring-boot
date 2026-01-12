package se.jensen.alexandra.springboot2.service;

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

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;

    public PostService(PostRepository postRepository, UserRepository userRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postMapper = postMapper;
    }
    //Finns redan i UserController/UserService
//    public List<PostResponseDTO> getAllPosts() {
//        List<Post> posts = postRepos3itory.findAll();
//        return posts.stream().map(post -> new PostResponseDTO(
//                post.getId(), post.getText(), post.getCreatedAt())).toList();
//    }
//
//    public PostResponseDTO findPostById(Long id) {
//        Optional<Post> post = postRepository.findById(id);
//        if (post.isPresent()) {
//            Post realPost = post.get();
//            return toDto(realPost);
//        } else {
//            throw new NoSuchElementException("Inget inlägg i databasen med id: " + id);
//        }
//    }

    public PostResponseDTO createPost(Long userId, PostRequestDTO postDto) {
        Post post = new Post();
        post.setText(postDto.text());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Användare finns ej med userId: " + userId));
        post.setUser(user);
        Post savedPost = postRepository.save(post);
        return new PostResponseDTO(savedPost.getId(), savedPost.getText(), savedPost.getCreatedAt());
    }

    public PostResponseDTO updatePost(PostRequestDTO userDto, Long id) {
        Optional<Post> existing = postRepository.findById(id);
        if (existing.isPresent()) {
            Post post = existing.get();
            postMapper.updateEntityFromDto(userDto, post);
            Post updatedPost = postRepository.save(post);
            return postMapper.toDto(updatedPost);
        } else {
            throw new NoSuchElementException("Inget inlägg i databasen med id: " + id);
        }
    }

    public void deletePostById(Long id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent()) {
            postRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("Inget inlägg i databasen med id: " + id);
        }
    }


}
