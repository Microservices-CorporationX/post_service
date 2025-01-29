package faang.school.postservice.service.kafka;

import faang.school.postservice.model.Comment;

public interface KafkaService {
    void sendPostEvent();
    void sendPostViewEvent();
    void sendLikeEvent();
    void sendCommentEvent(Comment comment);
}
