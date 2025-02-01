package faang.school.postservice.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.postservice.dto.post.PostRedis;
import faang.school.postservice.dto.user.UserNFDto;
import faang.school.postservice.event.PostCommentEvent;

import java.util.List;

public interface RedisCacheService {
    void savePost(PostRedis post);

    void saveUser(UserNFDto user);

    void incrementPostViews(Long postId);

    void incrementLike(Long postId);

    void addCommentForPost(PostCommentEvent event) throws JsonProcessingException;

    UserNFDto findUserById(long userId);

    List<PostRedis> findFeedByUserID(long userId, int countPosts);
}
