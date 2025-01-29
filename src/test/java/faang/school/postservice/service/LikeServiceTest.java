package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exceptions.CommentWasNotFoundException;
import faang.school.postservice.exceptions.PostWasNotFoundException;
import faang.school.postservice.exceptions.UserServiceConnectException;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostService postService;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private CommentService commentService;
    @InjectMocks
    private LikeService likeService;

    private List<UserDto> userList;

    private Stream<Like> likeStream;

    @BeforeEach
    public void setUp() {
        likeStream = Stream.of(
                Like.builder().id(1).userId(1L).build(),
                Like.builder().id(2).userId(2L).build(),
                Like.builder().id(3).userId(3L).build()
        );

        userList = List.of(
                new UserDto(1L, "user1", "user1@gmail.com"),
                new UserDto(2L, "user2", "user2@gmail.com"),
                new UserDto(3L, "user3", "user3@gmail.com")
        );
    }

    @Test
    @DisplayName("Test getLikedUsersToPost Success")
    public void testGetLikedUsersToPostSuccess() {
        when(postService.existsById(1L)).thenReturn(true);
        when(likeRepository.findAllByPostId(1L)).thenReturn(likeStream);
        when(userServiceClient.getUsersByIds(List.of(1L, 2L, 3L))).thenReturn(userList);

        List<UserDto> result = likeService.getLikedUsersToPost(1L);

        assertEquals(userList, result);
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Test getLikedUsersToPostNotExistsById Error")
    public void testGetLikedUsersToPostNotExistsById() {
        when(postService.existsById(2L)).thenReturn(false);

        PostWasNotFoundException exception = assertThrows(PostWasNotFoundException.class, () ->
                likeService.getLikedUsersToPost(2L)
        );

        assertEquals("Post with id 2 does not exist", exception.getMessage());
        verify(postService, times(1)).existsById(2L);
    }

    @Test
    @DisplayName("Test getLikedUsersToPostUserServiceError Error")
    public void testGetLikedUsersToPostUserServiceError() {
        when(postService.existsById(1L)).thenReturn(true);
        when(likeRepository.findAllByPostId(1L)).thenReturn(likeStream);
        when(userServiceClient.getUsersByIds(List.of(1L, 2L, 3L))).thenThrow(new RuntimeException());

        UserServiceConnectException exception = assertThrows(UserServiceConnectException.class, () ->
                likeService.getLikedUsersToPost(1L)
        );

        assertEquals("Failed users service", exception.getMessage());
        verify(userServiceClient, times(1)).getUsersByIds(List.of(1L, 2L, 3L));
    }

    @Test
    @DisplayName("Test getLikedUsersToComment Success")
    public void testGetLikedUsersToCommentSuccess() {
        when(commentService.existsById(1L)).thenReturn(true);
        when(likeRepository.findAllByCommentId(1L)).thenReturn(likeStream);
        when(userServiceClient.getUsersByIds(List.of(1L, 2L, 3L))).thenReturn(userList);

        List<UserDto> result = likeService.getLikedUsersToComment(1L);

        assertEquals(userList, result);
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Test getLikedUsersToCommentNotExistsById Error")
    public void testGetLikedUsersToCommentNotExistsById() {
        when(commentService.existsById(2L)).thenReturn(false);

        CommentWasNotFoundException exception = assertThrows(CommentWasNotFoundException.class, () ->
                likeService.getLikedUsersToComment(2L)
        );

        assertEquals("Comment with id 2 does not exist", exception.getMessage());
        verify(commentService, times(1)).existsById(2L);
    }

    @Test
    @DisplayName("Test getLikedUsersToCommentUserServiceError Error")
    public void testGetLikedUsersToCommentUserServiceError() {
        when(commentService.existsById(1L)).thenReturn(true);
        when(likeRepository.findAllByCommentId(1L)).thenReturn(likeStream);
        when(userServiceClient.getUsersByIds(List.of(1L, 2L, 3L))).thenThrow(new RuntimeException());

        UserServiceConnectException exception = assertThrows(UserServiceConnectException.class, () ->
                likeService.getLikedUsersToComment(1L)
        );

        assertEquals("Failed users service", exception.getMessage());
        verify(userServiceClient, times(1)).getUsersByIds(List.of(1L, 2L, 3L));
    }


}
