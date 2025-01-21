package faang.school.postservice.dto.newsfeed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedDto {
    private Long userId;
    private Long profilePicFileId;
    private Long profilePicSmallFileId;

    private Long postId;
    private Long authorId;
    private String content;
    private LocalDateTime publishedAt;
}
