package faang.school.postservice.producer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import faang.school.postservice.config.kafka.KafkaProperties;
import faang.school.postservice.dto.event.PostEventDto;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.LongStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PostEventProducerTest {
  @Mock
  private KafkaTemplate<String, Object> kafkaTemplate;

  @Mock
  private KafkaProperties kafkaProperties;

  @Mock
  private ExecutorService executorService;

  @InjectMocks
  private PostEventProducer postEventProducer;

  @Test
  @DisplayName("Should send event to Kafka in chunks")
  void testSendEvent() {
    int followersCount = 20_000;
    int batchSize = 100;
    ReflectionTestUtils.setField(postEventProducer, "batchSize", batchSize);
    ReflectionTestUtils.setField(postEventProducer, "cachedThreadPool",
        Executors.newCachedThreadPool());
    String topic = "post-topic";

    List<Long> followers = LongStream.rangeClosed(1, followersCount).boxed().toList();
    PostEventDto event = PostEventDto.builder()
        .posId(1L)
        .followers(followers)
        .build();

    List<Long> followersPart = LongStream.rangeClosed(1, batchSize).boxed().toList();
    PostEventDto eventPart = PostEventDto.builder()
        .posId(1L)
        .followers(followersPart)
        .build();

    List<Long> followersPartLast = LongStream.rangeClosed(followersCount - batchSize + 1, followersCount).boxed().toList();
    PostEventDto eventPartLast = PostEventDto.builder()
        .posId(1L)
        .followers(followersPartLast)
        .build();

    CompletableFuture<SendResult<String, Object>> result = new CompletableFuture<>();

    when(kafkaTemplate.send(any(), any())).thenReturn(result);
    when(kafkaProperties.getPostsTopic()).thenReturn(topic);

    postEventProducer.sendEvent(event);

    verify(kafkaTemplate, times(1)).send(topic, eventPart);
    verify(kafkaTemplate, times(1)).send(topic, eventPartLast);

  }
}