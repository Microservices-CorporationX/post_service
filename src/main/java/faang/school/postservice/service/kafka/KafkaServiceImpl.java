package faang.school.postservice.service.kafka;

import faang.school.postservice.event.CommentEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.producer.KafkaCommentProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaServiceImpl implements KafkaService{

    private final KafkaCommentProducer kafkaCommentProducer;

    @Override
    public void sendPostEvent() {

    }

    @Override
    public void sendPostViewEvent() {

    }

    @Override
    public void sendLikeEvent() {

    }

    @Override
//    @Async("executorKafkaSender) - ????????
    public void sendCommentEvent(Comment comment) {
        CommentEvent commentEvent = CommentEvent.builder()
                .id(comment.getId())
                .authorId(comment.getAuthorId())
                .postId(comment.getPost().getId())
                .content(comment.getContent())
                .updateAt(comment.getUpdatedAt())
                .build();

        kafkaCommentProducer.sendEvent(commentEvent);
    }
}
