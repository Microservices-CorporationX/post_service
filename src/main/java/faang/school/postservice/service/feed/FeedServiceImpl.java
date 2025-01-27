package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.kafka.producer.KafkaSender;
import faang.school.postservice.kafka.producer.KafkaSenderService;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    @Value("${heater.partitions:1000}")
    private int partitionSize;
    @Value("${spring.kafka.topics.feed.name:feed}")
    private String feedTopicName;
    private final PostService postService;
    private final UserServiceClient userServiceClient;
    private final KafkaSender kafkaSender;
    private final UserContext userContext;

    @Transactional
    public List<FeedDto> heat() {
        userContext.setUserId(1L);
        var feedList = userServiceClient.getUserIds().parallelStream()
                .map(userId -> FeedDto.builder()
                        .userId(userId)
                        .postIds(postService.getLastPostIds(userId, 500))
                        .build())
                .toList();
        ListUtils.partition(feedList, partitionSize).forEach(listOfFeeds -> {
            kafkaSender.sendMessage(feedTopicName, listOfFeeds);
        });
        return feedList;
    }
}
