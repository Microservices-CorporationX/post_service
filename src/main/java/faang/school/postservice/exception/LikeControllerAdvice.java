package faang.school.postservice.exception;

import faang.school.postservice.controller.LikeController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice(assignableTypes = LikeController.class)
@Slf4j
public class LikeControllerAdvice {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> exceptions = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                exceptions.put(error.getField(), error.getDefaultMessage())
        );

        log.error("Validation errors occurred: {}", exceptions);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptions);
    }
}
