package faang.school.postservice.service.post;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.redis.CachePost;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.CachePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
@RequiredArgsConstructor
public class PostCacheService {
    @Value(value = "${spring.data.redis.post-cache.comments-in-post:3}")
    private int maxCommentsInPostCache;
    private final CachePostRepository cachePostRepository;
    private final PostRepository postRepository;
    private final PostMapper mapper;

    public List<CachePost> getPostCacheByIds(List<Long> postIds) {
        List<CachePost> listOfCachePost = new ArrayList<>();
        for (Long postId : postIds) {
            CachePost cachePost = getCachePost(postId);
            listOfCachePost.add(cachePost);
        }
        return listOfCachePost;
    }

    public void incrementPostLikes(Long postId, Long likeId) {
        CachePost cachePost = getCachePost(postId);
        cachePost.getLikeIds().add(likeId);
        cachePost.incrementNumLikes();
        cachePostRepository.save(cachePost);
    }

    public void addPostView(Long postId) {
        CachePost cachePost = getCachePost(postId);
        cachePost.incrementNumViews();
        cachePostRepository.save(cachePost);
    }

    public void addCommentToPostCache(Long postId, CommentDto commentDto) {
        CachePost cachePost = getCachePost(postId);
        CopyOnWriteArraySet<CommentDto> comments = cachePost.getComments();
        if (comments == null) {
            comments = new CopyOnWriteArraySet<>();
        }
        checkCapacity(comments);
        addCommentAndSave(commentDto, comments, cachePost);
    }

    private void addCommentAndSave(CommentDto commentDto, CopyOnWriteArraySet<CommentDto> comments, CachePost cachePost) {
        comments.add(commentDto);
        cachePost.setComments(comments);
        cachePostRepository.save(cachePost);
    }

    private void checkCapacity(CopyOnWriteArraySet<CommentDto> comments) {
        if (comments.size() == maxCommentsInPostCache) {
            comments.stream().findFirst().ifPresent(comments::remove);
        }
    }

    private CachePost getCachePost(Long postId) {
        return cachePostRepository.save(cachePostRepository.findById(postId)
                .orElse(mapper.toCachePost(postRepository.findById(postId)
                        .orElseThrow(() -> new RuntimeException("There is no such post")))));

    }
}
