package faang.school.postservice.service.feed;

import faang.school.postservice.dto.event.PostEventDto;
import faang.school.postservice.model.redis.FeedCache;

public interface FeedService {

  //TODO receive event from Kafka and give it to update: dto= postId:[userId1, userId2...]
  // update for each user, the updateUserFeed() will be used async
  void processPostEvent(PostEventDto dto);

  //TODO update one user feed - will be used in processPostEvent()
  FeedCache updateUserFeed(Long userId, Long postId);

  //TODO get user feed from Redis to show
  FeedCache getUserFeed(Long userId, int startIdx, int postsNumber);


}
