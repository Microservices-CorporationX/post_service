package faang.school.postservice.exception;

public class FileProcessException extends RuntimeException {

    public FileProcessException(String message, Exception e) {
        super(message, e);
    }
}
