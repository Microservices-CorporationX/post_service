package faang.school.postservice.redis.entity;

import faang.school.postservice.dto.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RedisHash(value = "posts", timeToLive = 86400)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCache {
    @Id
    private Long id;
    private String content;
    private Long authorId;
    private Integer like;
    private Integer views;
    private List<CommentDto> comments = new CopyOnWriteArrayList<>();
}
