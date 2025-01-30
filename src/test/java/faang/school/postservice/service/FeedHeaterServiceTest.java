package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserForNewsFeedDto;
import faang.school.postservice.message.event.PostEvent;
import faang.school.postservice.message.producer.KafkaHeatPostProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.TaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedHeaterServiceTest {

    @Mock
    private PostService postService;

    @Mock
    private TaskExecutor threadPool;

    @Mock
    private NewsFeedService newsFeedService;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private KafkaHeatPostProducer heatPostProducer;

    @InjectMocks
    private FeedHeaterService feedHeaterService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(feedHeaterService, "usersPerEvent", 3);
        ReflectionTestUtils.setField(feedHeaterService, "postsPerThread", 5);
    }

    @Test
    void testHeat() {
        // arrange
        PostDto post = createPostDto(1L, 100L);
        UserForNewsFeedDto user = UserForNewsFeedDto.builder()
                .id(100L)
                .followerIds(List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L))
                .build();

        when(postService.getAllPosts()).thenReturn(List.of(post));
        when(userServiceClient.getUserForNewsFeed(100L)).thenReturn(user);

        // act
        feedHeaterService.heat();

        // assert
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(threadPool).execute(runnableCaptor.capture());
        runnableCaptor.getValue().run();

        ArgumentCaptor<PostEvent> eventCaptor = ArgumentCaptor.forClass(PostEvent.class);
        verify(heatPostProducer, times(3)).publishPostEvents(eventCaptor.capture());

        List<PostEvent> events = eventCaptor.getAllValues();
        assertThat(events).extracting(PostEvent::postId).containsOnly(1L);
        assertThat(events).extracting(PostEvent::followerIds)
                .containsExactly(List.of(1L, 2L, 3L), List.of(4L, 5L, 6L), List.of(7L));
    }

    @Test
    void testHeatNoPostsShouldNotProcessAnything() {
        // arrange
        when(postService.getAllPosts()).thenReturn(List.of());

        // act
        feedHeaterService.heat();

        // assert
        verify(threadPool, never()).execute(any());
        verifyNoInteractions(newsFeedService, userServiceClient, heatPostProducer);
    }

    private PostDto createPostDto(long postId, long authorId) {
        return PostDto.builder()
                .id(postId)
                .authorId(authorId)
                .content("Content")
                .build();
    }
}