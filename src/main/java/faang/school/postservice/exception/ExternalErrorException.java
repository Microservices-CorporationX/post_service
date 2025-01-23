package faang.school.postservice.exception;

public class ExternalErrorException extends RuntimeException {
    public ExternalErrorException(String message) {
        super(message);
    }
}