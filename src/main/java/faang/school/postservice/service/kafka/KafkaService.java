package faang.school.postservice.service.kafka;

import faang.school.postservice.model.Post;

public interface KafkaService {
    void sendPostEvent(Post post, long contextUserId);
    void sendPostViewEvent();
    void sendLikeEvent();
    void sendCommentEvent();
}
