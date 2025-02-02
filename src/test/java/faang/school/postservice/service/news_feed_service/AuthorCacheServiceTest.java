package faang.school.postservice.service.news_feed_service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.news_feed_models.NewsFeedAuthor;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.user.AuthorCacheMapper;
import faang.school.postservice.repository.cache_repository.AuthorCacheRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorCacheServiceTest {

    @Mock
    private AuthorCacheRepository authorCacheRepository;

    @Mock
    private AuthorCacheMapper authorCacheMapper;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private AuthorCacheService authorCacheService;

    @Test
    void saveAuthorCache_ShouldFetchUserAndSaveToCache() {
        Long authorId = 1L;
        UserDto authorDto = new UserDto(authorId, "TestUser", "test@example.com", "123456789", 5);
        NewsFeedAuthor newsFeedAuthor = new NewsFeedAuthor(authorId, "TestUser");

        when(userServiceClient.getUser(authorId)).thenReturn(authorDto);
        when(authorCacheMapper.toAuthorCache(authorDto)).thenReturn(newsFeedAuthor);

        authorCacheService.saveAuthorCache(authorId);

        verify(userServiceClient).getUser(authorId);
        verify(authorCacheMapper).toAuthorCache(authorDto);
        verify(authorCacheRepository).save(newsFeedAuthor);
    }

    @Test
    void getAuthorCacheById_WhenAuthorExists_ShouldReturnAuthorCache() {
        Long authorId = 1L;
        NewsFeedAuthor cachedAuthor = new NewsFeedAuthor(authorId, "TestUser");

        when(authorCacheRepository.findById(authorId)).thenReturn(Optional.of(cachedAuthor));

        NewsFeedAuthor result = authorCacheService.getAuthorCacheById(authorId);

        assertNotNull(result);
        assertEquals(authorId, result.getUserId());
        assertEquals("TestUser", result.getUsername());

        verify(authorCacheRepository).findById(authorId);
    }

    @Test
    void getAuthorCacheById_WhenAuthorDoesNotExist_ShouldReturnNull() {
        Long authorId = 2L;
        when(authorCacheRepository.findById(authorId)).thenReturn(Optional.empty());
        NewsFeedAuthor result = authorCacheService.getAuthorCacheById(authorId);
        assertNull(result);
        verify(authorCacheRepository).findById(authorId);
    }
}