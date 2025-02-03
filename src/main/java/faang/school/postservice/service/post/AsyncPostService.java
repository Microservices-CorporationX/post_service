package faang.school.postservice.service.post;

import faang.school.postservice.kafka.event.EventPostDto;
import faang.school.postservice.kafka.event.PostPublishedEvent;
import faang.school.postservice.kafka.producer.PostPublishedEventProducer;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.redis.PostCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncPostService {

    private final PostRepository postRepository;
    private final PostCacheService postCacheService;
    private final PostMapper postMapper;
    private final PostPublishedEventProducer postPublishedEventProducer;

    @Transactional
    @Async("threadPoolExecutorForPublishingPosts")
    public CompletableFuture<Void> publishPosts(List<Post> posts) {
        posts.forEach(Post::setPublished);
        postRepository.saveAll(posts);
        savePostsInCache(posts);
        sendPostPublishedEvent(posts);
        log.info("Published {} posts, by thread: {}", posts.size(), Thread.currentThread().getName());
        return CompletableFuture.completedFuture(null);
    }

    private void savePostsInCache(List<Post> posts) {
        posts.stream()
                .map(postMapper::toCachePost)
                .forEach(postCacheService::save);
    }

    private void sendPostPublishedEvent(List<Post> posts) {
        Map<Long, List<EventPostDto>> authorIdsToPostIds = posts.stream()
                .collect(Collectors.groupingBy(Post::getAuthorId)).entrySet().stream()
                .map(authorToPosts -> new AbstractMap.SimpleEntry<>(
                        authorToPosts.getKey(),
                        authorToPosts.getValue().stream()
                                .map(post -> new EventPostDto(post.getId(), post.getPublishedAt()))
                                .toList())
                ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
        authorIdsToPostIds.forEach((key, value) -> postPublishedEventProducer.send(new PostPublishedEvent(key, value)));
    }
}
