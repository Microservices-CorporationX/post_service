package faang.school.postservice.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDto {
    @Positive(message = "ID is required")
    private long id;

    private LocalDateTime scheduledAt;
}
