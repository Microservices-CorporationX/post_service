package faang.school.postservice.service;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.LikeMapperImpl;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.validator.LikeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private LikeValidator likeValidator;
    @Mock
    private LikeMapperImpl likeMapperImpl;
    @InjectMocks
    private LikeService likeService;
    private LikeDto likeDtoPost;
    private LikeDto likeDtoComment;

    @BeforeEach
    public void init() {
        likeDtoPost = LikeDto.builder().postId(1L).userId(1L).build();
        likeDtoComment = LikeDto.builder().commentId(1L).userId(1L).build();
    }

    @Test
    public void testCreateLikePostSuccess() {
        Like like = likeMapperImpl.toEntity(likeDtoPost);
        likeService.createPostLike(likeDtoPost);
        verify(likeValidator, times(1)).validateLikeCreationParams(likeDtoPost);
        verify(likeRepository, times(1)).save(like);
    }

    @Test
    public void testCreateLikeCommentSuccess() {
        Like like = likeMapperImpl.toEntity(likeDtoComment);
        likeService.createCommentLike(likeDtoComment);
        verify(likeValidator, times(1)).validateLikeCreationParams(likeDtoComment);
        verify(likeRepository, times(1)).save(like);
    }

    @Test
    public void testRemovePostLikeSuccess() {
        Mockito.when(userService.getUserDtoById(likeDtoPost.userId())).thenReturn(UserDto.builder().build());
        likeService.removePostLike(likeDtoPost);
        verify(likeValidator, times(1)).checkLikeBeforeDelete(likeDtoPost);
        verify(likeRepository, times(1)).deleteLikeByPostIdAndUserId(likeDtoPost.postId(), likeDtoPost.userId());
    }

    @Test
    public void testRemoveCommentLikeSuccess() {
        Mockito.when(userService.getUserDtoById(likeDtoComment.userId())).thenReturn(UserDto.builder().build());
        likeService.removeCommentLike(likeDtoComment);
        verify(likeValidator, times(1)).checkLikeBeforeDelete(likeDtoComment);
        verify(likeRepository, times(1)).deleteLikeByCommentIdAndUserId(likeDtoComment.commentId(), likeDtoComment.userId());
    }

}
