package faang.school.postservice.producer;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.subscription.SubscriptionUserDto;
import faang.school.postservice.events.PostEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KafkaPostProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserServiceClient userServiceClient;

    @Value("${spring.kafka.topic-name.posts-topic}")
    private String postsTopic;

    public void sendPostEvent(PostDto postDto) {
        List<SubscriptionUserDto> subscriptionsDto = userServiceClient.getFollowers(postDto.getId());
        List<Long> subscriptionsId = subscriptionsDto.stream()
                .map(SubscriptionUserDto::getId)
                .toList();

        PostEvent postEvent = new PostEvent(postDto.getId(), postDto.getContent(), subscriptionsId);

        kafkaTemplate.send(postsTopic, postEvent);
    }
}
