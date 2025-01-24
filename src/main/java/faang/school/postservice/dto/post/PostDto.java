package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PostDto {
    private final Long id;
    @NotEmpty(message = "Post cannot be empty")
    private final String content;
    private Long authorId;
    private Long projectId;
}
