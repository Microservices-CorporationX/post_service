package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.FeedCommentDto;
import faang.school.postservice.dto.post.FeedPostDto;
import faang.school.postservice.dto.user.ShortUserWithAvatarDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.user.ShortUserWithAvatar;
import faang.school.postservice.repository.PostCacheRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.comment.CommentCacheRepository;
import faang.school.postservice.repository.feed.FeedRepository;
import faang.school.postservice.repository.user.UserCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {
    private final UserContext userContext;
    private final FeedRepository feedRepository;
    private final PostCacheRepository postCacheRepository;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserCacheRepository userCacheRepository;
    private final UserServiceClient userServiceClient;
    private final CommentCacheRepository commentCacheRepository;

    @Value("${feed.page-size}")
    private int pageSize;

    public List<FeedPostDto> getFeed(Long postId) {
        log.info("Getting feed for user with id {}", userContext.getUserId());
        List<FeedPostDto> feed = new ArrayList<>(getPostsFromFeed(postId));
        if (feed.size() < pageSize) {
            addPostsFromDb(feed, postId);
        }
        feed.forEach(post -> {
            fillUser(post);
            fillComments(post);
        });
        return feed;
    }

    private List<FeedPostDto> getPostsFromFeed(Long postId) {
        List<Long> postIdsFromFeed = new ArrayList<>(feedRepository.getFeed(userContext.getUserId(), postId));
        return new ArrayList<>(IterableUtils
                .toList(postCacheRepository.findAllById(postIdsFromFeed))
                .stream()
                .map(postMapper::cacheToFeedPostDto)
                .toList());
    }

    private void addPostsFromDb(List<FeedPostDto> feed, Long postId) {
        Long lastPostId = postId;
        if (!feed.isEmpty()) {
            lastPostId = feed.get(feed.size() - 1).getId();
        }
        List<Post> postsFromDb;
        int limit = pageSize - feed.size();
        if (lastPostId == null) {
            postsFromDb = postRepository.findLatestPostsForFeed(userContext.getUserId(), limit);
        } else {
            postsFromDb = postRepository.findPostsForFeed(lastPostId, userContext.getUserId(), limit);
        }
        feed.addAll(postsFromDb.stream().map(postMapper::toFeedPostDto).toList());
    }

    private void fillUser(FeedPostDto feedPostDto) {
        ShortUserWithAvatarDto shortUserWithAvatarDto;
        Optional<ShortUserWithAvatar> userFromCache = userCacheRepository.findById(feedPostDto.getAuthorId());
        shortUserWithAvatarDto = userFromCache.map(shortUserWithAvatar -> new ShortUserWithAvatarDto(
                        shortUserWithAvatar.getId(),
                        shortUserWithAvatar.getUsername(),
                        shortUserWithAvatar.getSmallAvatarId()))
                .orElseGet(() -> userServiceClient.getShortUserWithAvatarById(feedPostDto.getAuthorId()));
        feedPostDto.setUser(shortUserWithAvatarDto);
    }

    private void fillComments(FeedPostDto feedPostDto) {
        if (feedPostDto.getComments() != null) {
            return;
        }
        feedPostDto.setComments(
                commentCacheRepository.getCommentsForPost(feedPostDto.getId()).stream()
                        .map(comment -> new FeedCommentDto(
                                comment.getId(),
                                comment.getAuthorId(),
                                comment.getContent(),
                                comment.getCreatedAt())
                        ).toList()
        );
    }
}
