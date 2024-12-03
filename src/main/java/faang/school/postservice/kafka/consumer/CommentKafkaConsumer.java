package faang.school.postservice.kafka.consumer;

import faang.school.postservice.model.dto.redis.cache.RedisUserDto;
import faang.school.postservice.model.entity.UserShortInfo;
import faang.school.postservice.model.event.kafka.CommentSentKafkaEvent;
import faang.school.postservice.service.RedisPostService;
import faang.school.postservice.service.RedisUserService;
import faang.school.postservice.service.UserShortInfoService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentKafkaConsumer extends AbstractKafkaConsumer<CommentSentKafkaEvent> {
    private static final int REFRESH_TIME_IN_HOURS = 3;

    private final RedisUserService redisUserService;
    private final RedisPostService redisPostService;
    private final UserShortInfoService userShortInfoService;

    @Override
    @KafkaListener(
            topics = "${kafka.topics.comment}",
            groupId = "${kafka.consumer.groups.post-service.group-id}",
            concurrency = "${kafka.consumer.groups.post-service.concurrency}"
    )
    public void consume(ConsumerRecord<String, CommentSentKafkaEvent> record, Acknowledgment acknowledgment) {
        super.consume(record, acknowledgment);
        acknowledgment.acknowledge();
    }

    @Override
    protected void processEvent(CommentSentKafkaEvent event) {
        UserShortInfo userShortInfo = userShortInfoService
                .updateUserShortInfoIfStale(event.getCommentAuthorId(), REFRESH_TIME_IN_HOURS);
        redisUserService.updateUserIfStale(userShortInfo, REFRESH_TIME_IN_HOURS);
        redisPostService.addComment(event.getPostId(), event.getCommentId(), event.getCommentContent());
    }
}
