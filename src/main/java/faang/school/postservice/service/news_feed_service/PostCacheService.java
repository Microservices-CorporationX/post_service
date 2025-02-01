package faang.school.postservice.service.news_feed_service;

import faang.school.postservice.dto.news_feed_models.NewsFeedPost;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.post.NewsFeedPostMapper;
import faang.school.postservice.repository.cache_repository.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostCacheService {
    private final PostCacheRepository postCacheRepository;
    private final NewsFeedPostMapper newsFeedPostMapper;

    public void savePostCache(PostResponseDto postResponseDto) {
        NewsFeedPost newsFeedPost = newsFeedPostMapper.toCache(postResponseDto);
        postCacheRepository.save(newsFeedPost);
        log.info("Post cache with id: {} saved to cache ", postResponseDto.getId());
    }

    public NewsFeedPost getPostCacheByPostId(Long postId) {
        return postCacheRepository.findById(postId).orElse(null);
    }

    public void updateCountViews(Long postId, Long count) {
        NewsFeedPost newsFeedPost = postCacheRepository.findById(postId).orElse(null);
        if(newsFeedPost != null) {
            newsFeedPost.setCountViews(count);
            postCacheRepository.save(newsFeedPost);
        }
    }

    public void deletePostCacheByPostId(Long postId) {
        postCacheRepository.deleteById(postId);
    }
}