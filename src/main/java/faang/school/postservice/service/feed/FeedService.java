package faang.school.postservice.service.feed;

import faang.school.postservice.dto.post.PostFeedDto;

import java.util.List;

public interface FeedService {
    void bindPostToFollower(Long followerId, Long postId);

    List<PostFeedDto> getPosts(Long postId, long userId);
}
