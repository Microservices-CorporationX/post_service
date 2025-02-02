package faang.school.postservice.producer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
@EnableConfigurationProperties(KafkaProperties.class)
public class PostEventProducerTest {

  @Autowired
  private KafkaProperties kafkaProperties;

  @Mock
  private KafkaTemplate<String, Object> kafkaTemplate;

  @Mock
  private ExecutorService executorService;

  @InjectMocks
  private PostEventProducer postEventProducer;

  @Test
  @DisplayName("Should send event to Kafka in chunks")
  void test() {

    System.out.println(kafkaProperties.bootstrapAddress());
    System.out.println(this.kafkaProperties.postTopic());
    int followersCount = 20_000;
    int batchSize = 100;

    ReflectionTestUtils.setField(postEventProducer, "batchSize", batchSize);
    ReflectionTestUtils.setField(postEventProducer, "kafkaProperties", kafkaProperties);
    ReflectionTestUtils.setField(postEventProducer, "cachedThreadPool",
        Executors.newCachedThreadPool());

    String topic = "post_topic";

    List<Long> followers = LongStream.rangeClosed(1, followersCount).boxed().toList();
    PostEventDto event = PostEventDto.builder()
        .posId(1L)
        .followers(followers)
        .build();

    CompletableFuture<SendResult<String, Object>> result = new CompletableFuture<>();
    result.complete(null);

    when(kafkaTemplate.send(any(), any())).thenReturn(result);

    postEventProducer.sendEvent(event);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    verify(kafkaTemplate, times(200)).send(eq(topic), any(PostEventDto.class));
  }



//  @Test
//  @DisplayName("Should send event to Kafka in chunks")
//  void testSendEvent() {
//    int followersCount = 20_000;
//    int batchSize = 100;
//
//    ReflectionTestUtils.setField(postEventProducer, "batchSize", batchSize);
//    ReflectionTestUtils.setField(postEventProducer, "cachedThreadPool",
//        Executors.newCachedThreadPool());
//    String topic = "post-topic";
//
////// Настройка ExecutorService для синхронного выполнения задач - вместо reflection
////    when(executorService.submit(any(Runnable.class))).thenAnswer(invocation -> {
////      Runnable task = invocation.getArgument(0);
////      task.run(); // Выполняем задачу синхронно
////      return null;
////    });
//
//    List<Long> followers = LongStream.rangeClosed(1, followersCount).boxed().toList();
//    PostEventDto event = PostEventDto.builder()
//        .posId(1L)
//        .followers(followers)
//        .build();
//
//    List<Long> followersPart = LongStream.rangeClosed(1, batchSize).boxed().toList();
//    PostEventDto eventPart = PostEventDto.builder()
//        .posId(1L)
//        .followers(followersPart)
//        .build();
//
//    List<Long> followersPartLast = LongStream.rangeClosed(followersCount - batchSize + 1,
//        followersCount).boxed().toList();
//    PostEventDto eventPartLast = PostEventDto.builder()
//        .posId(1L)
//        .followers(followersPartLast)
//        .build();
//
////    when(kafkaProperties.postTopic()).thenReturn(topic);
//
//    CompletableFuture<SendResult<String, Object>> result = new CompletableFuture<>();
//    result.complete(null); // Завершаем Future
//
//    when(kafkaTemplate.send(any(), any())).thenReturn(result);
//
//    postEventProducer.sendEvent(event);
//
//    verify(kafkaTemplate, times(1)).send(topic, eventPart);
//    verify(kafkaTemplate, times(1)).send(topic, eventPartLast);
//
////    verify(kafkaTemplate, times(200)).send(eq(topic), any(PostEventDto.class));
//
////    ArgumentCaptor<PostEventDto> eventCaptor = ArgumentCaptor.forClass(PostEventDto.class);
////
////    verify(kafkaTemplate, times(200)).send(eq(topic), eventCaptor.capture());
////
////    List<PostEventDto> capturedEvents = eventCaptor.getAllValues();
////    assertEquals(batchSize, capturedEvents.get(0).getFollowers().size()); // Проверка первого батча
////    assertEquals(batchSize, capturedEvents.get(199).getFollowers().size()); // Проверка последнего батча
//
//  }
}