package faang.school.postservice.exeption;

public class PostWasDeletedException extends RuntimeException {
    public PostWasDeletedException(String message) {
        super(message);
    }
}