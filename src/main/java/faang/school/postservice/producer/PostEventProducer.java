package faang.school.postservice.producer;

import faang.school.postservice.dto.event.PostEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostEventProducer extends AbstractEventProducer<PostEvent> {

  public PostEventProducer(KafkaTemplate<String, Object> kafkaTemplate, NewTopic topic) {
    super(kafkaTemplate, topic);
  }

  @Override
  public void sendEvent(PostEvent event) {
    super.sendEvent(event);
  }
}
