package faang.school.postservice.dto.Post;

import faang.school.postservice.dto.ad.AdDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedPostDto {
    private long id;
    private String content;
    private long authorId;
    private long projectId;
    private long likesCount;
    private long commentsCount;
    private AdDto adDto;
    private String publishedAt;
}
