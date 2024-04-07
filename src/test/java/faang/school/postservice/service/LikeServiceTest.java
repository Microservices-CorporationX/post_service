package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validation.LikeValidation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {
    @Mock
    private PostService postService;
    @Mock
    private CommentService commentService;
    @Mock
    private LikeValidation likeValidation;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private UserContext userContext;
    @Spy
    private LikeMapper likeMapper;

    @InjectMocks
    private LikeService likeService;

    private Like like;
    private LikeDto likeDto;
    private Post post;
    private Comment comment;
    private UserDto userDto;
    private long userId = 1L;

    @BeforeEach
    public void setUp() {
        likeDto = LikeDto.builder()
                .id(1L)
                .userId(userId)
                .postId(3L)
                .commentId(4L)
                .build();

        userDto = UserDto.builder()
                .id(userId)
                .build();

        post = Post.builder()
                .id(3L)
                .build();

        comment = Comment.builder()
                .id(4L)
                .build();

        like = Like.builder()
                .id(1L)
                .userId(userId)
                .post(post)
                .comment(comment)
                .build();
    }

    @Test
    @DisplayName("Like the post")
    public void testLikePost() {
        when(postService.getPost(likeDto.getPostId())).thenReturn(post);
        when(userServiceClient.getUser(likeDto.getUserId())).thenReturn(userDto);
        when(likeRepository.save(any(Like.class))).thenReturn(like);
        when(likeMapper.toDto(like)).thenReturn(likeDto);

        assertEquals(likeDto, likeService.likePost(likeDto));

        verify(likeValidation, times(1)).verifyUniquenessLikePost(likeDto.getPostId(), likeDto.getUserId());
    }
}
