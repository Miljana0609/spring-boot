package se.jensen.alexandra.springboot2.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.jensen.alexandra.springboot2.repository.PostRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void findPostById_ShouldThrowException_WhenIdDoesNotExist() {
        // Arrange
        Long nonExistingId = 99L;
        // Simulera att databasen returnerar en tom Optional
        when(postRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> postService.findPostById(nonExistingId));

        assertEquals("Inget inlägg i databasen med id: 99", exception.getMessage());
    }
}
