package faang.school.postservice.dto.post;

import lombok.NonNull;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class RequestPostDto {
    Long id;
    @NonNull
    String content;
    Long authorId;
    Long projectId;
    boolean published;
    LocalDateTime publishedAt;
    LocalDateTime scheduledAt;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}