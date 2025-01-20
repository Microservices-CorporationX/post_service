package faang.school.postservice.excpetions;

public class PostWasNotFoundException extends RuntimeException {
    public PostWasNotFoundException(String message) {
        super(message);
    }
}
