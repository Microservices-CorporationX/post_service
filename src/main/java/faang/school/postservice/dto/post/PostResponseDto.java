package faang.school.postservice.dto.post;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostResponseDto {
    private Long id;
    private Long authorId;
    private Long projectId;
    private LocalDateTime publishedDate;
    private Boolean published;
    private String content;
    private Boolean deleted;
}