package faang.school.postservice.exception;

public class PostBadRequestException extends RuntimeException {
    public PostBadRequestException(String message) {
        super(message);
    }
}
