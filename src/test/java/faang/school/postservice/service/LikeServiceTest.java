package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeCommentDto;
import faang.school.postservice.dto.like.LikePostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private PostService postService;

    @Mock
    private CommentService commentService;

    @Spy
    private LikeMapper likeMapper = Mappers.getMapper(LikeMapper.class);

    @InjectMocks
    private LikeService likeService;

    private long userId;
    private long postId;
    private long commentId;
    private long authorId;
    Post post;
    Comment comment;
    Like savedLikePost;
    Like savedLikeComment;

    @BeforeEach
    public void setUp() {
        userId = 1L;
        postId = 5L;
        commentId = 6L;
        authorId = 7L;

        post = Post.builder()
                .id(postId)
                .authorId(authorId)
                .build();

        comment = Comment.builder()
                .id(commentId)
                .build();

        savedLikePost = Like.builder()
                .id(25L)
                .userId(userId)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();

        savedLikeComment = Like.builder()
                .id(26L)
                .userId(userId)
                .comment(comment)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void testCreateLikePostWhenCreateLikeSaveSuccessful() {
        when(userServiceClient.getUser(userId)).thenReturn(UserDto.builder().build());
        when(postService.isPostNotExist(postId)).thenReturn(false);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());
        when(postService.getPostById(postId)).thenReturn(post);
        when(likeRepository.save(any(Like.class))).thenReturn(savedLikePost);

        LikeEventPublisher likeEventPublisher = mock(LikeEventPublisher.class);
        likeService = new LikeService(likeRepository, userServiceClient, postService, commentService, likeMapper, likeEventPublisher);

        LikePostDto result = likeService.createLikePost(postId, userId);

        verify(userServiceClient).getUser(userId);
        verify(postService).isPostNotExist(postId);
        verify(likeRepository).findByPostIdAndUserId(postId, userId);
        verify(postService).getPostById(postId);
        verify(likeRepository).save(any(Like.class));

        LikeEvent expectedEvent = new LikeEvent(postId, post.getAuthorId(), userId, LocalDateTime.now());
        verify(likeEventPublisher).publish(any(LikeEvent.class));

        assertNotNull(result);
        assertEquals(savedLikePost.getId(), result.id());
        assertEquals(savedLikePost.getUserId(), result.userId());
        assertEquals(savedLikePost.getPost().getId(), result.postId());
    }

    @Test
    public void testCreateLikePostWithNonExistentPost() {
        long nonExistentPost = 123L;
        when(postService.isPostNotExist(nonExistentPost)).thenReturn(true);

        assertThrows(EntityNotFoundException.class,
                () -> likeService.createLikePost(nonExistentPost, userId));
    }

    @Test
    public void testCreateLikePostWhenLikeExists() {
        when(likeRepository.findByPostIdAndUserId(postId, userId))
                .thenReturn(Optional.ofNullable(savedLikePost));

        assertThrows(DataValidationException.class, () -> likeService.createLikePost(postId, userId));
    }

    @Test
    public void testCreateLikeCommentWhenCreateLikeSaveSuccessful() {
        when(userServiceClient.getUser(userId)).thenReturn(UserDto.builder().build());
        when(commentService.isCommentNotExist(commentId)).thenReturn(false);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.empty());
        when(commentService.getCommentById(commentId)).thenReturn(comment);
        when(likeRepository.save(any(Like.class))).thenReturn(savedLikeComment);

        LikeCommentDto result = likeService.createLikeComment(commentId, userId);

        verify(userServiceClient).getUser(userId);
        verify(commentService).isCommentNotExist(commentId);
        verify(likeRepository).findByCommentIdAndUserId(commentId, userId);
        verify(commentService).getCommentById(commentId);
        verify(likeRepository).save(any(Like.class));

        assertNotNull(result);
        assertEquals(savedLikeComment.getId(), result.id());
        assertEquals(savedLikeComment.getUserId(), result.userId());
        assertEquals(savedLikeComment.getComment().getId(), result.commentId());
    }

    @Test
    public void testCreateLikeCommentWithNonExistentComment() {
        long nonExistentComment = 123L;
        when(commentService.isCommentNotExist(nonExistentComment)).thenReturn(true);

        assertThrows(EntityNotFoundException.class,
                () -> likeService.createLikeComment(nonExistentComment,userId));
    }

    @Test
    public void testCreateLikeCommentWhenLikeExists() {
        when(likeRepository.findByCommentIdAndUserId(commentId, userId))
                .thenReturn(Optional.ofNullable(savedLikeComment));

        assertThrows(DataValidationException.class,
                () -> likeService.createLikeComment(commentId, userId));
    }
}