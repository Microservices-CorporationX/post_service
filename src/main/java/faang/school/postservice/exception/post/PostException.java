package faang.school.postservice.exception.post;

public class PostException extends RuntimeException {
    public PostException(String message, Object... args) {
        super(String.format(message, args));
    }

}
