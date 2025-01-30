package faang.school.postservice.cache.comment;

import faang.school.postservice.cache.CacheHandler;
import faang.school.postservice.cache.user.UserCacher;
import faang.school.postservice.dto.post.PostCache;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.comment.CommentCacheRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CommentCacher extends CacheHandler<PostCache> {

    @Value("${spring.data.redis.cache.comment.limit:3}")
    private Long commentLimitation;
    private final Long cacheTtl;
    private final CommentCacheRepository commentCacheRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserCacher userCacher;


    @Override
    @Transactional
    @SneakyThrows
    @Async("cacheExecutor")
    protected void cache(PostCache postCache) {
        var lastComments = commentRepository.findLastComments(postCache.getId(), commentLimitation)
                .orElseThrow(() -> new EntityNotFoundException("Comments not found"));
        lastComments.forEach(comment -> {
            var commentCache = commentMapper.toCache(comment);
            commentCache.setTtl(cacheTtl);
            userCacher.cache(PostCache.builder().authorId(comment.getAuthorId()).build());
            cacheData(commentCache, commentCacheRepository::save);
        });
    }
}
