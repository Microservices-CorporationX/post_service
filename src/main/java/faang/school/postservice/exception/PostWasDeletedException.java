package faang.school.postservice.exception;

public class PostWasDeletedException extends RuntimeException {
    public PostWasDeletedException(String message) {
        super(message);
    }
}