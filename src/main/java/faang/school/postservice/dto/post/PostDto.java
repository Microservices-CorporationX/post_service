package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDto {
    private Long id;
    private String content;
    private String authorId;
    private String projectId;
    private LocalDateTime createdAt;
    private boolean published;
    private boolean deleted;
    private LocalDateTime publishedAt;
}
