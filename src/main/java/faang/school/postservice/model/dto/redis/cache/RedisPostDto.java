package faang.school.postservice.model.dto.redis.cache;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.postservice.model.enums.AuthorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RedisPostDto {
    private Long postId;
    private Long authorId;
    private String content;
//TODO publishedAt
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    private int commentCount;
    private int likeCount;
    private List<String> recentComments = new ArrayList<>();
    private int viewCount;
}
