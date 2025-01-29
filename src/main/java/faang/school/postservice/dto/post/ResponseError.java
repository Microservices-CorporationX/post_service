package faang.school.postservice.dto.post;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class ResponseError {
    String message;
    String path;
    LocalDateTime timestamp;
}