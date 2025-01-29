package faang.school.postservice.exceptions;

import faang.school.postservice.dto.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserServiceConnectException.class)
    public ResponseEntity<?> handlePostNotFoundException(UserServiceConnectException e) {
        return ResponseEntity.badRequest().body(new Message(false, e.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

}
