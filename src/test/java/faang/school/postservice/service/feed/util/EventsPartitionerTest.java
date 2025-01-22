package faang.school.postservice.service.feed.util;

import faang.school.postservice.dto.post.message.NewPostMessage;
import faang.school.postservice.dto.post.message.UsersFeedUpdateMessage;
import faang.school.postservice.dto.post.message.counter.CommentCountersKeysMessage;
import faang.school.postservice.dto.post.message.counter.CommentLikeCounterKeysMessage;
import faang.school.postservice.dto.post.message.counter.PostLikeCountersKeysMessage;
import faang.school.postservice.dto.post.message.counter.PostViewCountersKeysMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventsPartitionerTest {
    private static final int LIMIT = 10;
    private static final long POST_ID = 1;
    private static final long AUTHOR_ID = 2;
    private static final long TIMESTAMP = 123456789;
    private static final List<Long> USER_IDS = List.of(1L, 2L, 3L);
    private static final List<String> KEYS = List.of("key:1", "key:2", "key:3");

//    @BeforeEach
//    void setUp() {
//        ReflectionTestUtils.setField(eventsPartitioner, "followerPartitionsLimit", LIMIT);
//        ReflectionTestUtils.setField(eventsPartitioner, "viewCountersPartitionLimit", LIMIT);
//        ReflectionTestUtils.setField(eventsPartitioner, "likeCountersPartitionLimit", LIMIT);
//        ReflectionTestUtils.setField(eventsPartitioner, "commentCountersPartitionLimit", LIMIT);
//        ReflectionTestUtils.setField(eventsPartitioner, "commentLikesPartitionLimit", LIMIT);
//        ReflectionTestUtils.setField(eventsPartitioner, "usersFeedUpdatePartitionLimit", LIMIT);
//    }
//
//    @InjectMocks
//    private EventsPartitioner eventsPartitioner;
//
//    @Test
//    void test_partitionSubscribersAndMapToMessage_successful() {
//        NewPostMessage newPostMessage = NewPostMessage.builder()
//                .postId(POST_ID)
//                .authorId(AUTHOR_ID)
//                .createdAtTimestamp(TIMESTAMP)
//                .followersIds(USER_IDS)
//                .build();
//
//        assertThat(eventsPartitioner.partitionSubscribersAndMapToMessage(POST_ID, AUTHOR_ID, TIMESTAMP, USER_IDS))
//                .isEqualTo(List.of(newPostMessage));
//    }
//
//    @Test
//    void test_partitionViewCounterKeysAndMapToMessage_successful() {
//        PostViewCountersKeysMessage postViewCountersKeysMessage = new PostViewCountersKeysMessage(KEYS);
//
//        assertThat(eventsPartitioner.partitionViewCounterKeysAndMapToMessage(KEYS))
//                .isEqualTo(List.of(postViewCountersKeysMessage));
//    }
//
//    @Test
//    void test_partitionLikeCounterKeysAndMapToMessage_successful() {
//        PostLikeCountersKeysMessage message = new PostLikeCountersKeysMessage(KEYS);
//
//        assertThat(eventsPartitioner.partitionLikeCounterKeysAndMapToMessage(KEYS))
//                .isEqualTo(List.of(message));
//    }
//
//    @Test
//    void test_partitionCommentCounterKeysAndMapToMessage_successful() {
//        CommentCountersKeysMessage message = new CommentCountersKeysMessage(KEYS);
//
//        assertThat(eventsPartitioner.partitionCommentCounterKeysAndMapToMessage(KEYS))
//                .isEqualTo(List.of(message));
//    }
//
//    @Test
//    void test_partitionCommentLikeCounterKeysAndMapToMessage_successful() {
//        CommentLikeCounterKeysMessage message = new CommentLikeCounterKeysMessage(KEYS);
//
//        assertThat(eventsPartitioner.partitionCommentLikeCounterKeysAndMapToMessage(KEYS))
//                .isEqualTo(List.of(message));
//    }
//
//    @Test
//    void test_partitionUserIdsAndMapToMessage_successful() {
//        UsersFeedUpdateMessage message = new UsersFeedUpdateMessage(USER_IDS);
//
//        assertThat(eventsPartitioner.partitionUserIdsAndMapToMessage(USER_IDS))
//                .isEqualTo(List.of(message));
//    }
}