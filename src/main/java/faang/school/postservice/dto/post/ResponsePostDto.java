package faang.school.postservice.dto.post;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class ResponsePostDto {
    Long id;
    String content;
    Long authorId;
    Long projectId;
    boolean published;
    LocalDateTime publishedAt;
    LocalDateTime scheduledAt;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}