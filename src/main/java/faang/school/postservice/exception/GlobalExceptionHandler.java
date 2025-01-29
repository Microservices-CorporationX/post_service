package faang.school.postservice.exception;

import faang.school.postservice.dto.post.ResponseError;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseError handleEntityNotFoundException(EntityNotFoundException e, HttpServletRequest request) {
        log.error("Exception occurred while handling request at {}: {}", request.getRequestURI(), e.getMessage(), e);

        return new ResponseError(
                "Not found: " + e.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseError handleUserNotFoundException(UserNotFoundException e, HttpServletRequest request) {
        log.error("Exception occurred while handling request at {}: {}", request.getRequestURI(), e.getMessage(), e);

        return new ResponseError(
                "Not found: " + e.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseError handleProjectNotFoundException(ProjectNotFoundException e, HttpServletRequest request) {
        log.error("Exception occurred while handling request at {}: {}", request.getRequestURI(), e.getMessage(), e);

        return new ResponseError(
                "Not found: " + e.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
    }
}