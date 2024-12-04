package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private LikeMapper likeMapper;

    @InjectMocks
    private LikeService likeService;

    // likeComment
    @Test
    void shouldLikeCommentSuccessfully() {
        Long commentId = 1L;
        LikeDto likeDto = createLikeDto(1L);
        Comment comment = mock(Comment.class);
        Like likeEntity = new Like();

        when(userServiceClient.getUser(anyLong())).thenReturn(null);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(likeRepository.findByCommentIdAndUserId(commentId, likeDto.getUserId())).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(likeEntity);
        when(likeMapper.toDto(any(Like.class))).thenReturn(likeDto);

        LikeDto result = likeService.likeComment(commentId, likeDto);
        assertEquals(likeDto, result);
    }

    @Test
    void shouldFailWhenUserAlreadyLikedComment() {
        Long commentId = 1L;
        LikeDto likeDto = createLikeDto(1L);
        Comment comment = mock(Comment.class);
        Like existingLike = new Like();

        when(userServiceClient.getUser(anyLong())).thenReturn(null);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(likeRepository.findByCommentIdAndUserId(commentId, likeDto.getUserId())).thenReturn(Optional.of(existingLike));

        assertThrows(IllegalArgumentException.class, () -> likeService.likeComment(commentId, likeDto));
    }

    @Test
    void shouldFailWhenCommentNotFound() {
        Long commentId = 1L;
        LikeDto likeDto = createLikeDto(1L);

        when(userServiceClient.getUser(anyLong())).thenReturn(null);
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> likeService.likeComment(commentId, likeDto));
    }

    @Test
    void shouldFailWhenUserNotFound() {
        Long commentId = 1L;
        LikeDto likeDto = createLikeDto(1L);

        doThrow(new IllegalArgumentException("User does not exist"))
                .when(userServiceClient).getUser(anyLong());

        assertThrows(IllegalArgumentException.class, () -> likeService.likeComment(commentId, likeDto));
    }

    // unlikeComment
    @Test
    void shouldUnlikeCommentSuccessfully() {
        Long commentId = 1L;
        LikeDto likeDto = createLikeDto(1L);
        Comment comment = mock(Comment.class);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        doNothing().when(likeRepository).deleteByCommentIdAndUserId(commentId, likeDto.getUserId());

        likeService.unlikeComment(commentId, likeDto);
        verify(likeRepository, times(1)).deleteByCommentIdAndUserId(commentId, likeDto.getUserId());
    }

    @Test
    void shouldFailWhenCommentNotFoundDuringUnlike() {
        Long commentId = 1L;
        LikeDto likeDto = createLikeDto(1L);

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> likeService.unlikeComment(commentId, likeDto));
    }

    @Test
    void shouldFailWhenUserNotFoundDuringUnlike() {
        Long commentId = 1L;
        LikeDto likeDto = createLikeDto(1L);

        doThrow(new IllegalArgumentException("User does not exist"))
                .when(userServiceClient).getUser(anyLong());

        assertThrows(IllegalArgumentException.class, () -> likeService.unlikeComment(commentId, likeDto));
    }

    private LikeDto createLikeDto(Long userId) {
        LikeDto likeDto = new LikeDto();
        likeDto.setUserId(userId);
        return likeDto;
    }
}