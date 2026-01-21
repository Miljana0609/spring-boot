package se.jensen.alexandra.springboot2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * En klass som hanterar fel i hela applikationen på ett samlat sätt.
 * Istället för att varje Controller behöver hantera fel, fångar den här klassen upp
 * vanliga undantag och skickar tillbaka ett tydligt svar till frontend.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Metod som fångar valideringsfel när data från frontend inte stämmer överens med regler.
     *
     * @param ex - Undantaget som innehåller felet
     * @return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors) - returnerar ett objekt
     * med vilka fält som är fel och varför med statuskod 400 (Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        for (org.springframework.validation.FieldError fieldError
                : ex.getBindingResult().getFieldErrors()) {

            String fieldName = fieldError.getField();
            String message = fieldError.getDefaultMessage();

            errors.put(fieldName, message);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Metod som fångar när ett objekt inte kan hittas i databasen (t.ex. användare eller inlägg)
     *
     * @param ex - Undantaget som kastats
     * @return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage ()) - Returnerar
     * felmeddelande med statuskod 404 (Not Found)
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Fångar ogiltiga argument som skickas till metoder.
     *
     * @param ex - Undantaget som kastats
     * @return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage ()) - Returnerar
     * felmeddelande med statuskod 400 (Bad Request)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
