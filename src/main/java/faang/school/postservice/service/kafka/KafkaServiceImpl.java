package faang.school.postservice.service.kafka;

import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.producer.KafkaCommentProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaServiceImpl implements KafkaService {

    private final KafkaCommentProducer kafkaCommentProducer;
    private final CommentMapper commentMapper;

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
    @Async("executorKafkaSend")
    public void sendCommentEvent(Comment comment) {
        kafkaCommentProducer.send(commentMapper.toCommentEvent(comment));
    }
}
