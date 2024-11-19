package faang.school.postservice.kafka;

import faang.school.postservice.kafka.dto.PostKafkaDto;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class KafkaPostProducer {
    @Value("${kafka.topic.post-published-topic}")
    private String postPublishedTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPostsToKafka(List<Post> posts) {
        for (Post post : posts) {
            PostKafkaDto postKafkaDto = build(post);
            kafkaTemplate.send(postPublishedTopic, postKafkaDto);
        }
    }

    private PostKafkaDto build(Post post) {
        return PostKafkaDto.builder()
                .id(post.getId())
                .build();
    }
}
