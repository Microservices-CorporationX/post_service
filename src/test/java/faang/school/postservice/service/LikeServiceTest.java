package faang.school.postservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.OutboxEventRepository;
import faang.school.postservice.validator.CommentValidator;
import faang.school.postservice.validator.PostValidator;
import faang.school.postservice.validator.UserValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private PostService postService;

    @Mock
    private CommentService commentService;

    @Mock
    private LikeMapper likeMapper;

    @Mock
    private LikeEventPublisher likeEventPublisher;

    @Mock
    private PostValidator postValidator;

    @Mock
    private UserValidator userValidator;

    @Mock
    private CommentValidator commentValidator;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @InjectMocks
    private LikeService likeService;

    @Test
    void shouldReturnEmptyListWhenNoLikesForPost() {
        when(likeRepository.findByPostId(anyLong())).thenReturn(List.of());

        List<UserDto> result = likeService.getUsersWhoLikePostByPostId(1L);

        assertTrue(result.isEmpty(), "Result should be empty when there are no likes");
        verify(likeRepository, times(1)).findByPostId(1L);
        verifyNoInteractions(userServiceClient);
    }

    @Test
    void shouldReturnUserDtosForSingleBatchOfLikesForPost() {
        long postId = 1L;
        List<Like> likes = createLikes(List.of(1L, 2L));
        List<UserDto> userDtos = createUserDtos(List.of(1L, 2L));

        when(likeRepository.findByPostId(postId)).thenReturn(likes);
        when(userServiceClient.getUsersByIds(anyList())).thenReturn(userDtos);

        List<UserDto> result = likeService.getUsersWhoLikePostByPostId(postId);

        assertEquals(userDtos, result, "Result should match the expected user DTOs");
        verify(likeRepository, times(1)).findByPostId(postId);
        verify(userServiceClient, times(1)).getUsersByIds(List.of(1L, 2L));
    }

    @Test
    void shouldReturnUserDtosForSingleBatchOfLikesForComment() {
        long commentId = 1L;
        List<Like> likes = createLikes(List.of(1L, 2L));
        List<UserDto> userDtos = createUserDtos(List.of(1L, 2L));

        when(likeRepository.findByCommentId(commentId)).thenReturn(likes);
        when(userServiceClient.getUsersByIds(anyList())).thenReturn(userDtos);

        List<UserDto> result = likeService.getUsersWhoLikeComments(commentId);

        assertEquals(userDtos, result, "Result should match the expected user DTOs");
        verify(likeRepository, times(1)).findByCommentId(commentId);
        verify(userServiceClient, times(1)).getUsersByIds(List.of(1L, 2L));
    }

    @Test
    void shouldHandleMultipleBatchesForLikesForPost() {
        long postId = 1L;
        List<Long> userIds = new ArrayList<>();
        for (long i = 1; i <= 150; i++) {
            userIds.add(i);
        }

        List<Like> likes = createLikes(userIds);
        List<UserDto> userDtos = createUserDtos(userIds);

        when(likeRepository.findByPostId(postId)).thenReturn(likes);
        when(userServiceClient.getUsersByIds(anyList())).thenAnswer(invocation -> {
            List<Long> ids = invocation.getArgument(0);
            return userDtos.stream()
                    .filter(dto -> ids.contains(dto.getId()))
                    .collect(Collectors.toList());
        });

        List<UserDto> result = likeService.getUsersWhoLikePostByPostId(postId);

        assertEquals(150, result.size(), "Result size should match the number of likes");
        assertEquals(userDtos, result, "Result should match the expected user DTOs");
        verify(likeRepository, times(1)).findByPostId(postId);
        verify(userServiceClient, times(2)).getUsersByIds(anyList());
    }

    private List<UserDto> createUserDtos(List<Long> userIds) {
        return userIds.stream()
                .map(id -> UserDto.builder()
                        .id(id)
                        .username("User" + id)
                        .email("user" + id + "@test.com")
                        .build())
                .collect(Collectors.toList());
    }

    private List<Like> createLikes(List<Long> userIds) {
        return userIds.stream()
                .map(userId -> {
                    Like like = new Like();
                    like.setUserId(userId);
                    return like;
                })
                .collect(Collectors.toList());
    }

    @Test
    void addLikeToPostShouldAddLikeAndPublishEvent() {
        LikeDto likeDto = LikeDto.builder()
                .userId(2L)
                .commentId(3L)
                .build();

        Post post = Post.builder()
                .id(1L)
                .authorId(4L)
                .build();

        Comment comment = Comment.builder()
                .id(3L)
                .build();

        Like like = Like.builder()
                .userId(2L)
                .post(post)
                .comment(comment)
                .build();

        LikeResponseDto likeResponseDto = LikeResponseDto.builder()
                .id(10L)
                .userId(2L)
                .postId(1L)
                .commentId(3L)
                .build();

        when(postService.getPostById(1L)).thenReturn(post);
        when(commentService.getCommentById(3L)).thenReturn(comment);
        when(likeRepository.save(like)).thenReturn(like);
        when(likeMapper.toDto(like)).thenReturn(likeResponseDto);

        LikeResponseDto result = likeService.addLikeToPost(1L, likeDto);

        verify(likeRepository, times(1)).save(like);

        assertEquals(likeResponseDto, result);
    }
}