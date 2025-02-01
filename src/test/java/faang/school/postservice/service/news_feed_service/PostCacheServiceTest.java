package faang.school.postservice.service.news_feed_service;

import faang.school.postservice.dto.news_feed_models.NewsFeedPost;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.post.NewsFeedPostMapper;
import faang.school.postservice.repository.cache_repository.PostCacheRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostCacheServiceTest {

    @Mock
    private PostCacheRepository postCacheRepository;

    @Mock
    private NewsFeedPostMapper newsFeedPostMapper;

    @InjectMocks
    private PostCacheService postCacheService;

    @Test
    void savePostCache_ShouldMapAndSavePostCache() {
        PostResponseDto postResponseDto = new PostResponseDto();
        postResponseDto.setId(1L);
        NewsFeedPost newsFeedPost = new NewsFeedPost();
        newsFeedPost.setPostId(1L);

        when(newsFeedPostMapper.toCache(postResponseDto)).thenReturn(newsFeedPost);

        postCacheService.savePostCache(postResponseDto);

        verify(newsFeedPostMapper).toCache(postResponseDto);
        verify(postCacheRepository).save(newsFeedPost);
    }

    @Test
    void getPostCacheByPostId_WhenPostExists_ShouldReturnPostCache() {
        Long postId = 1L;
        NewsFeedPost cachedPost = new NewsFeedPost();
        cachedPost.setPostId(postId);

        when(postCacheRepository.findById(postId)).thenReturn(Optional.of(cachedPost));

        NewsFeedPost result = postCacheService.getPostCacheByPostId(postId);

        assertNotNull(result);
        assertEquals(postId, result.getPostId());

        verify(postCacheRepository).findById(postId);
    }

    @Test
    void getPostCacheByPostId_WhenPostDoesNotExist_ShouldReturnNull() {
        Long postId = 2L;

        when(postCacheRepository.findById(postId)).thenReturn(Optional.empty());

        NewsFeedPost result = postCacheService.getPostCacheByPostId(postId);

        assertNull(result);

        verify(postCacheRepository).findById(postId);
    }

    @Test
    void updateCountViews_WhenPostExists_ShouldUpdateAndSavePostCache() {
        Long postId = 1L;
        Long newViewCount = 100L;
        NewsFeedPost cachedPost = new NewsFeedPost();
        cachedPost.setPostId(postId);
        cachedPost.setCountViews(50L);

        when(postCacheRepository.findById(postId)).thenReturn(Optional.of(cachedPost));

        postCacheService.updateCountViews(postId, newViewCount);

        assertEquals(newViewCount, cachedPost.getCountViews());
        verify(postCacheRepository).findById(postId);
        verify(postCacheRepository).save(cachedPost);
    }

    @Test
    void updateCountViews_WhenPostDoesNotExist_ShouldNotAttemptSave() {
        Long postId = 2L;
        Long newViewCount = 200L;

        when(postCacheRepository.findById(postId)).thenReturn(Optional.empty());

        postCacheService.updateCountViews(postId, newViewCount);

        verify(postCacheRepository).findById(postId);
        verify(postCacheRepository, never()).save(any(NewsFeedPost.class));
    }

    @Test
    void deletePostCacheByPostId_ShouldCallDeleteById() {
        Long postId = 1L;

        postCacheService.deletePostCacheByPostId(postId);

        verify(postCacheRepository).deleteById(postId);
    }
}