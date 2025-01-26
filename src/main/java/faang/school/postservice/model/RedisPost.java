package faang.school.postservice.model;

import faang.school.postservice.dto.CommentDto;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

//пока захардкожено так как нельзя строку в параметр ttl передавать
//есть вариант через redisCacheManager bean сделать как лучше ?
@RedisHash(value = "Posts", timeToLive = 86400)
@Data
public class RedisPost {
    @Id
    private long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private List<CommentDto> commentsDto = new CopyOnWriteArrayList<>();
    private LocalDateTime CreatedAt;
    private LocalDateTime updatedAt;
    private long views;
    private long likes;
}
