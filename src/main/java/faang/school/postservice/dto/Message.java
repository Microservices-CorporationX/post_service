package faang.school.postservice.dto;


public record Message(
        boolean status,
        String message,
        int code
) {
}
