package faang.school.postservice.event.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.model.LikeEvent;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class LikeEventSubscriber implements MessageListener {

    private final ObjectMapper objectMapper = new ObjectMapper();
    public static List<String> messageList = new ArrayList<>();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            LikeEvent event = objectMapper.readValue(message.getBody(), LikeEvent.class);
            messageList.add(event.toString());
            log.info("Received LikeEvent: {}", event);
        } catch (Exception e) {
            log.error("Error processing message", e);
        }
    }
}
