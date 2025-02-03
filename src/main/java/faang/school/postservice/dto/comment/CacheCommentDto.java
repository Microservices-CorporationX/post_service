package faang.school.postservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CacheCommentDto {

    private Long id;
    private String content;
    private Long authorId;
    private Long postId;
    private Long likesCount;
    private LocalDateTime updatedAt;
}
