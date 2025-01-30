package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserForNewsFeedDto;
import faang.school.postservice.message.event.PostEvent;
import faang.school.postservice.message.producer.KafkaHeatPostProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedHeaterService {

    private final PostService postService;
    private final TaskExecutor threadPool;
    private final NewsFeedService newsFeedService;
    private final UserServiceClient userServiceClient;
    private final KafkaHeatPostProducer heatPostProducer;

    @Value("${spring.kafka.topic.posts.users-per-event}")
    private int usersPerEvent;

    @Value("${spring.kafka.topic.heat.posts-per-thread}")
    private int postsPerThread;

    public void heat() {
        log.info("Starting cache heat for news feed");
        List<PostDto> posts = postService.getAllPosts();
        List<List<PostDto>> listOfPosts = ListUtils.partition(posts, postsPerThread);
        listOfPosts.forEach(postsList -> threadPool.execute(() -> processBatchOfPosts(postsList)));
    }

    private void processBatchOfPosts(List<PostDto> posts) {
        for (var post : posts) {
            newsFeedService.savePostToCache(post);

            UserForNewsFeedDto user = userServiceClient.getUserForNewsFeed(post.authorId());
            newsFeedService.saveUserToCache(user);

            List<List<Long>> batches = ListUtils.partition(user.followerIds(), usersPerEvent);
            List<PostEvent> postEvents = mapPostIdsToBatchOfPostEvents(batches, post);
            postEvents.forEach(heatPostProducer::publishPostEvents);
        }
    }

    private List<PostEvent> mapPostIdsToBatchOfPostEvents(List<List<Long>> batches, PostDto post) {
        return batches.stream()
                .map(batch -> new PostEvent(post.id(), batch))
                .toList();
    }
}
