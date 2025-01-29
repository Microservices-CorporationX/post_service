package faang.school.postservice.utils;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FeedHeater {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final UserServiceClient userServiceClient;

    public void heatCacheForSpecificUsers(List<Long> userIds) {
        List<UserDto> users = userServiceClient.getUsersByIds(userIds);
        for (UserDto user : users) {
            kafkaTemplate.send("feed-heat-topic", user.getId().toString());
        }
    }
}
