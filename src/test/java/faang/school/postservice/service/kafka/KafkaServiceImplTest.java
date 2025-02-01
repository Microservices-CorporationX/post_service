package faang.school.postservice.service.kafka;

import faang.school.postservice.event.CommentEvent;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaCommentProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaServiceImplTest {
    @Mock
    private KafkaCommentProducer kafkaCommentProducer;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private KafkaServiceImpl kafkaService;

    @Test
    void sendCommentEvent_SuccessTest() {
        Comment comment = getComment();
        Post post = getPost();
        comment.setPost(post);
        CommentEvent commentEvent = getCommentEvent();

        when(commentMapper.toCommentEvent(any())).thenReturn(commentEvent);

        kafkaService.sendCommentEvent(comment);

        verify(kafkaCommentProducer).send(any(CommentEvent.class));
        verify(commentMapper, times(1)).toCommentEvent(any());
    }

    @Test
    void sendCommentEvent_WhenProducerThrowsException_FailTest() {
        Comment comment = getComment();

        Post post = getPost();
        comment.setPost(post);
        CommentEvent commentEvent = getCommentEvent();

        when(commentMapper.toCommentEvent(any())).thenReturn(commentEvent);
        doThrow(new RuntimeException("Kafka producer error")).when(kafkaCommentProducer).send(any(CommentEvent.class));

        assertThrows(RuntimeException.class, () -> {
            kafkaService.sendCommentEvent(comment);
        });

        verify(kafkaCommentProducer).send(any(CommentEvent.class));
        verify(commentMapper, times(1)).toCommentEvent(any());
    }

    private Comment getComment() {
        return Comment.builder()
                .id(1L)
                .authorId(123L)
                .content("Test comment content")
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Post getPost() {
        return Post.builder()
                .id(456L)
                .build();
    }

    private CommentEvent getCommentEvent() {
        return CommentEvent.builder()
                .id(1L)
                .postId(456L)
                .authorId(123L)
                .content("Test comment event content")
                .updatedAt(LocalDateTime.now())
                .build();
    }
}