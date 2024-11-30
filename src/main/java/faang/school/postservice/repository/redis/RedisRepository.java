package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.post.PostDto;

import java.util.List;
import java.util.Set;

public interface RedisRepository {
    void saveAll(Long id, List<PostDto> posts);

    void add(Long followerId, Long postId);

    Set<Long> find(Long id);

    Long getRank(Long id, Long postId);

    Set<Object> getRange(Long id, long startPostId, long endPostId);
}
