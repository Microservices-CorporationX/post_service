package faang.school.postservice.service.feed.cache;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.feedheat.FeedHeatEvent;
import faang.school.postservice.helper.UserCacheWriter;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.CommentCache;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.PostCache;
import faang.school.postservice.publisher.feedheat.FeedHeatPublisher;
import faang.school.postservice.repository.PostCacheRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.comment.CommentCacheRepository;
import faang.school.postservice.repository.feed.FeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HeaterService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostCacheRepository postCacheRepository;
    private final FeedRepository feedRepository;
    private final CommentCacheRepository commentCacheRepository;
    private final UserCacheWriter userCacheWriter;
    private final UserServiceClient userServiceClient;
    private final FeedHeatPublisher feedHeatPublisher;

    @Value("${caching.feed.size}")
    private int feedSize;

    @Value("${caching.comment.size}")
    private int commentCacheSize;

    @Value("${caching.heat.page-size}")
    private int pageSize;

    @Value("${caching.heat.batch-size}")
    private int heaterIdsBatchSize;

    public void startHeat() {
        List<Long> ids;
        long currentPage = 0;
        do {
            ids = getUserIds(currentPage, pageSize);
            log.info("Sending batches of user ids for cache heating. Ids: {}", ids);
            ListUtils.partition(ids, heaterIdsBatchSize).forEach(
                    partition -> feedHeatPublisher.publish(new FeedHeatEvent(partition))
            );
            currentPage++;
        } while (ids.size() == pageSize);
        log.info("Finished sending batches of user ids for cache heating");
    }

    @Transactional
    @Async("heatCacheThreadPool")
    public void heatUser(long userId) {
        log.info("Heating cache for user with id {}", userId);
        List<Post> postsFromDb = postRepository.findLatestPostsForFeed(userId, commentCacheSize);
        List<PostCache> postsForCache = postsFromDb.stream().map(postMapper::toPostCache).toList();
        postCacheRepository.saveAll(postsForCache);
        postsForCache.forEach(post -> feedRepository.addPostToUserFeed(userId, post.getId(), post.getPublishedAt()));
        postsFromDb.forEach(this::cacheComments);
        userCacheWriter.cacheUser(userId);
    }

    private void cacheComments(Post post) {
        if (post.getComments() == null) {
            return;
        }
        post.getComments().stream()
                .sorted((comment1, comment2) -> comment1.getCreatedAt().isAfter(comment2.getCreatedAt()) ? 1 : -1)
                .limit(commentCacheSize)
                .map(comment -> new CommentCache(
                        comment.getId(),
                        comment.getAuthorId(),
                        comment.getContent(),
                        comment.getCreatedAt())
                )
                .forEach(comment -> commentCacheRepository.save(post.getId(), comment));
    }

    @Retryable(maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    private List<Long> getUserIds(long page, long pageSize) {
        try {
            return userServiceClient.getUserIds(page, pageSize);
        } catch (Exception e) {
            log.error("Error when getting user from UserService", e);
            throw e;
        }
    }
}
