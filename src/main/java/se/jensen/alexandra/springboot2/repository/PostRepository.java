package se.jensen.alexandra.springboot2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.jensen.alexandra.springboot2.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

}
