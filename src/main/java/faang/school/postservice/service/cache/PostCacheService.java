package faang.school.postservice.service.cache;

import faang.school.postservice.cache_entities.PostCache;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.post.PostCacheMapper;
import faang.school.postservice.repository.cache_repository.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostCacheService {
    private final PostCacheRepository postCacheRepository;
    private final PostCacheMapper postCacheMapper;

    public void savePostCache(PostResponseDto postResponseDto) {
        PostCache postCache = postCacheMapper.toCache(postResponseDto);
        postCacheRepository.save(postCache);
        log.info("Post cache with id: {} saved to cache ", postResponseDto.getId());

    }

    public PostCache getPostCacheByPostId(Long postId) {
        return postCacheRepository.findById(postId).orElse(null);
    }

    public void updateCountViews(Long postId, Long count) {
        PostCache postCache = postCacheRepository.findById(postId).orElse(null);
        if(postCache != null) {
            postCache.setCountViews(count);
            postCacheRepository.save(postCache);
        }
    }

    public void deletePostCacheByPostId(Long postId) {
        postCacheRepository.deleteById(postId);
    }
}