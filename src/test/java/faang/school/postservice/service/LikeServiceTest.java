package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.mapper.LikeMapperImpl;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validator.LikeValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;

    @Mock
    private LikeValidator likeValidator;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private PostService postService;

    @Mock
    private CommentService commentService;

    @Spy
    private LikeMapperImpl likeMapper;

    @Mock
    private UserServiceClient userServiceClient;
    private Like firstLike;
    private Like secondLike;
    private UserDto firstUserDto;
    private UserDto secondUserDto;

    @BeforeEach
    void setUp() {
        firstLike = new Like();
        secondLike = new Like();
        firstUserDto = new UserDto();
        secondUserDto = new UserDto();
    }

    @Test
    public void testGetLikeByIdUserNotExists() {
        long userId = 1L;
        when(likeRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> likeService.getLikeById(userId));
    }

    @Test
    public void testGetLikeById() {
        long userId = 1L;
        Like like = new Like();
        when(likeRepository.findById(userId)).thenReturn(Optional.of(like));

        likeService.getLikeById(userId);
    }

    @Test
    public void testGetAllLikes() {
        List<Like> likes = List.of(new Like(), new Like());
        when(likeRepository.findAll()).thenReturn(likes);
        likeService.getAllLikes();
    }

    @Test
    public void testAddLike() {
        long userId = 1L;
        long postId = 2L;
        LikeDto likeDto = new LikeDto();
        likeDto.setPostId(postId);

        when(userContext.getUserId()).thenReturn(userId);
        when(postService.getPost(postId)).thenReturn(new Post());

        likeService.addLike(likeDto);

        verify(likeRepository).save(any(Like.class));
    }

    @Test
    public void testDeleteLike() {
        LikeDto likeDto = new LikeDto();
        likeDto.setId(1L);

        when(likeRepository.existsById(likeDto.getId())).thenReturn(true);

        likeService.deleteLike(likeDto);
        verify(likeRepository).deleteById(likeDto.getId());
    }

    @Test
    void testGetAllLikedByPostId() {
        Long id = 1L;
        firstLike.setId(1L);
        firstLike.setUserId(1L);
        secondLike.setId(2L);
        secondLike.setUserId(2L);
        List<Like> likes = List.of(firstLike, secondLike);

        when(likeRepository.findByPostId(id)).thenReturn(likes);

        firstUserDto.setId(1L);
        secondUserDto.setId(2L);
        List<UserDto> users = List.of(firstUserDto, secondUserDto);

        when(userServiceClient.getUsersByIds(List.of(1L, 2L))).thenReturn(users);

        List<UserDto> result = likeService.getAllLikedByPostId(id);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(likeRepository, times(1)).findByPostId(id);
        verify(userServiceClient, times(1)).getUsersByIds(List.of(1L, 2L));
    }

    @Test
    void testGetAllLikedByCommentId() {
        Long id = 1L;
        firstLike.setId(1L);
        firstLike.setUserId(1L);
        secondLike.setId(2L);
        secondLike.setUserId(2L);
        List<Like> likes = List.of(firstLike, secondLike);

        when(likeRepository.findByCommentId(id)).thenReturn(likes);

        firstUserDto.setId(1L);
        secondUserDto.setId(2L);
        List<UserDto> users = List.of(firstUserDto, secondUserDto);

        when(userServiceClient.getUsersByIds(List.of(1L, 2L))).thenReturn(users);

        List<UserDto> result = likeService.getAllLikedByCommentId(id);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(likeRepository, times(1)).findByCommentId(id);
        verify(userServiceClient, times(1)).getUsersByIds(List.of(1L, 2L));
    }
}
