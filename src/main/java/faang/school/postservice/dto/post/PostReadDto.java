package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class PostReadDto {
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private List<Long> hashtagIds;
    private LocalDateTime createdAt;
    private boolean published;
    private boolean deleted;
    private LocalDateTime publishedAt;
}
