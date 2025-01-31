package faang.school.postservice.service.feed.cache;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.feedheat.FeedHeatEvent;
import faang.school.postservice.helper.UserCacheWriter;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.mapper.post.PostMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.CommentCache;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.PostCache;
import faang.school.postservice.publisher.feedheat.FeedHeatPublisher;
import faang.school.postservice.repository.PostCacheRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.comment.CommentCacheRepository;
import faang.school.postservice.repository.feed.FeedRepository;
import org.apache.commons.collections4.ListUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.LongStream;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HeaterServiceTest {
    private static final int FEED_SIZE = 10;
    private static final int COMMENT_CACHE_SIZE = 3;
    private static final int PAGE_SIZE = 4;
    private static final int HEATER_IDS_BATCH_SIZE = 2;

    @Mock
    private PostRepository postRepository;
    @Spy
    private PostMapperImpl postMapper;
    @Mock
    private PostCacheRepository postCacheRepository;
    @Mock
    private FeedRepository feedRepository;
    @Mock
    private CommentCacheRepository commentCacheRepository;
    @Mock
    private UserCacheWriter userCacheWriter;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private FeedHeatPublisher feedHeatPublisher;
    @InjectMocks
    private HeaterService heaterService;

    @BeforeEach
    public void beforeEach() {
        ReflectionTestUtils.setField(heaterService, "feedSize", FEED_SIZE);
        ReflectionTestUtils.setField(heaterService, "commentCacheSize", COMMENT_CACHE_SIZE);
        ReflectionTestUtils.setField(heaterService, "pageSize", PAGE_SIZE);
        ReflectionTestUtils.setField(heaterService, "heaterIdsBatchSize", HEATER_IDS_BATCH_SIZE);
    }

    @Test
    void testStartHeat() {
        List<Long> idsFromUserService1 = LongStream.range(0, PAGE_SIZE).boxed().toList();
        List<Long> idsFromUserService2 = LongStream.range(PAGE_SIZE, 2 * PAGE_SIZE - 1).boxed().toList();
        when(userServiceClient.getUserIds(0, PAGE_SIZE)).thenReturn(idsFromUserService1);
        when(userServiceClient.getUserIds(1, PAGE_SIZE)).thenReturn(idsFromUserService2);

        heaterService.startHeat();

        ListUtils.partition(idsFromUserService1, HEATER_IDS_BATCH_SIZE).forEach(batch ->
                verify(feedHeatPublisher, times(1)).publish(new FeedHeatEvent(batch))
        );
        ListUtils.partition(idsFromUserService2, HEATER_IDS_BATCH_SIZE).forEach(batch ->
                verify(feedHeatPublisher, times(1)).publish(new FeedHeatEvent(batch))
        );
    }

    @Test
    void testHeatUser() {
        long userId = 1L;
        Post postWithComments = Post
                .builder()
                .id(3L)
                .publishedAt(LocalDateTime.now())
                .comments(List.of
                        (
                                Comment.builder().id(1L).authorId(1L).content("content1").createdAt(LocalDateTime.now()).build(),
                                Comment.builder().id(2L).authorId(1L).content("content2").createdAt(LocalDateTime.now()).build()
                        ))
                .build();
        List<Post> postsFromDb = List.of
                (
                        Post.builder().id(1L).publishedAt(LocalDateTime.now()).build(),
                        Post.builder().id(2L).publishedAt(LocalDateTime.now()).build(),
                        postWithComments
                );
        when(postRepository.findLatestPostsForFeed(userId, COMMENT_CACHE_SIZE)).thenReturn(postsFromDb);

        heaterService.heatUser(userId);

        List<PostCache> postsForCache = postsFromDb.stream().map(postMapper::toPostCache).toList();
        verify(postCacheRepository).saveAll(postsForCache);
        postsFromDb.forEach(
                post -> verify(feedRepository).addPostToUserFeed(userId, post.getId(), post.getPublishedAt())
        );
        postWithComments.getComments().forEach(
                comment -> verify(commentCacheRepository).save(postWithComments.getId(), new CommentCache
                        (
                                comment.getId(),
                                comment.getAuthorId(),
                                comment.getContent(),
                                comment.getCreatedAt()
                        )
                )
        );
        verify(userCacheWriter).cacheUser(userId);
    }
}