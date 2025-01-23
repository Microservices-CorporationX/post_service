package faang.school.postservice.exception;

public class UserUnauthorizedAccessException extends RuntimeException {
    public UserUnauthorizedAccessException(String message) {
        super(message);
    }
}
