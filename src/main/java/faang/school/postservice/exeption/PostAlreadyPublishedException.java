package faang.school.postservice.exeption;

public class PostAlreadyPublishedException extends RuntimeException {
    public PostAlreadyPublishedException(String message) {
        super(message);
    }
}