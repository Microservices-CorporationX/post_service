package faang.school.postservice.service.banner;

import faang.school.postservice.dto.user.message.UsersForBanMessage;
import faang.school.postservice.message.broker.publisher.user.UsersBanMessageToKafkaPublisher;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorBanner {
    private final PostService postService;
    private final UsersBanMessageToKafkaPublisher publisher;

    @Scheduled(cron = "${redis.banner.schedule}")
    public void publishingUsersForBan() {
        List<Long> userIdsForBan = postService.findUserIdsForBan();

        if (userIdsForBan.isEmpty()) return;

        UsersForBanMessage message = UsersForBanMessage.builder()
                .userIds(userIdsForBan)
                .build();

        publisher.publish(message);
    }
}