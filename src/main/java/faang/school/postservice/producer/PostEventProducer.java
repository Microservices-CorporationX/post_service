package faang.school.postservice.producer;

import faang.school.postservice.config.kafka.KafkaProperties;
import faang.school.postservice.dto.event.PostEventDto;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostEventProducer {

  @Value("${newsfeed.subscribers.batch-size:10000}")
  private int batchSize;

  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final KafkaProperties kafkaProperties;

  private final ExecutorService cachedThreadPool;

  public void sendEvent(PostEventDto postEvent) {
    log.info("Sending New Post Event with Followers to Kafka");

    List<PostEventDto> batches = splitIntoBatches(postEvent);
    String topic = kafkaProperties.getPostsTopic();

    List<CompletableFuture<Void>> futures = batches.stream()
        .map(event -> CompletableFuture.runAsync(
            () -> kafkaTemplate.send(topic, event), cachedThreadPool))
        .toList();

    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
        .thenRun(() -> log.info("all messages delivered to Kafka"));
  }

  private List<PostEventDto> splitIntoBatches(PostEventDto postEventDto) {
    Long postId = postEventDto.getPosId();
    List<Long> followers = postEventDto.getFollowers();

    int followersCount = followers.size();

    int batchNumbs = (followersCount + batchSize - 1) / batchSize;

    List<PostEventDto> postEventBatches = new ArrayList<>();

    for (int i = 0; i < batchNumbs; i++) {
      int start = i * batchSize;
      int end = Math.min(followersCount, (i + 1) * batchSize);

      List<Long> followersSublist = followers.subList(start, end);

      PostEventDto postEventPart = PostEventDto.builder()
          .posId(postId)
          .followers(followersSublist)
          .build();

      postEventBatches.add(postEventPart);
    }
    return postEventBatches;
  }

}