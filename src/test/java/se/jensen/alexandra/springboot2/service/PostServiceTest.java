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

/**
 * Testklass som ansvarar för att testa metoderna i PostService.
 * Använder mockade beroenden för att simulera interaktioner med PostRepository.
 */
@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;  // Mock av PostRepository för att simulera interaktion med databasen.

    @InjectMocks
    private PostService postService;  // Den klass som testas, PostService, där mockade beroenden injiceras.

    /**
     * Testar att ett NoSuchElementException kastas när ett inlägg inte kan hittas med ett givet ID.
     * Den simulerar ett scenario där det inte finns något inlägg med det angivna ID:t i databasen.
     *
     * @throws Exception om något går fel under testets körning.
     */
    @Test
    void findPostById_ShouldThrowException_WhenIdDoesNotExist() {
        // Arrange
        Long nonExistingId = 99L;  // Definierar ett ID som inte finns i databasen.

        // Simulerar att repositoryn returnerar en tom Optional när ID:t inte finns.
        when(postRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        // Förväntar oss att NoSuchElementException kastas med ett specifikt felmeddelande.
        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> postService.findPostById(nonExistingId));

        // Verifierar att det kastade undantaget innehåller det förväntade felmeddelandet.
        assertEquals("Inget inlägg i databasen med id: 99", exception.getMessage());
    }
}
