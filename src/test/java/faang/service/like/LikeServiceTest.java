package faang.service.like;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.like.LikeService;
import faang.school.postservice.service.post.PostService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private UserContext userContext;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private PostService postService;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private LikeService likeService;

    private UserDto userDto;

    private long userId;
    private long postId;
    private  long commentId;
    private Post post;
    private Like like;

    @BeforeEach
    void setUp() {
        commentId = 1L;
        postId = 1L;
        userId = 1L;
        post = new Post();
        post.setId(postId);

        like = new Like();
        like.setPost(post);
        like.setUserId(userId);
        userDto = new UserDto(1L, "david", "@mail.ru");
    }

    @Test
    void testSaveLikePost() {
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(postRepository.existsById(postId)).thenReturn(true);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());
        when(postService.getPostById(postId)).thenReturn(post);
        when(likeRepository.save(any(Like.class))).thenReturn(like);

        Like result = likeService.saveLikePost(postId);

        assertNotNull(result);
        assertEquals(postId, result.getPost().getId());
        assertEquals(userId, result.getUserId());
    }

    @Test
    void testPostNotFound() {
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(postRepository.existsById(postId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> likeService.saveLikePost(postId));
    }

    @Test
    void testLikePostAlreadyExists() {
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(postRepository.existsById(postId)).thenReturn(true);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(
            Optional.of(new Like()));

        assertThrows(EntityExistsException.class, () -> likeService.saveLikePost(postId));
    }

    @Test
    void testSaveLikeComment() {

        Comment comment = new Comment();
        comment.setId(commentId);
        Like like = new Like();
        like.setComment(comment);
        like.setUserId(userId);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(commentRepository.existsById(commentId)).thenReturn(true);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(
            Optional.empty());
        when(commentService.getComment(commentId)).thenReturn(comment);
        when(likeRepository.save(any(Like.class))).thenReturn(like);

        Like result = likeService.saveLikeComment(commentId);

        assertNotNull(result);
        assertEquals(commentId, result.getComment().getId());
        assertEquals(userId, result.getUserId());
    }

    @Test
    void testCommentNotFound() {
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(commentRepository.existsById(commentId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> likeService.saveLikeComment(commentId));
    }

    @Test
    void testCommentLikeAlreadyExists() {
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(commentRepository.existsById(commentId)).thenReturn(true);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(
            Optional.of(new Like()));

        assertThrows(EntityExistsException.class, () -> likeService.saveLikeComment(commentId));
    }

    @Test
    void testDeleteLikePost() {
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.of(new Like()));

        likeService.deleteLikePost(postId);

        verify(likeRepository, times(1)).deleteByPostIdAndUserId(postId, userId);
    }

    @Test
    void testDeleteLikePost_LikeNotFound() {
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());

        assertThrows(EntityExistsException.class, () -> likeService.deleteLikePost(postId));
    }

    @Test
    void testDeleteLikeComment() {
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.of(new Like()));

        likeService.deleteLikeComment(commentId);

        verify(likeRepository, times(1)).deleteByCommentIdAndUserId(commentId, userId);
    }

    @Test
    void testDeleteLikeComment_LikeNotFound() {
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.empty());

        assertThrows(EntityExistsException.class, () -> likeService.deleteLikeComment(commentId));
    }

    @Test
    void testCountLikesPost() {
        Post post = new Post();
        post.setId(postId);
        post.setPublished(true);
        post.setLikes(List.of(new Like(), new Like()));

        when(postRepository.existsById(postId)).thenReturn(true);
        when(postService.getPostById(postId)).thenReturn(post);

        Long result = likeService.countLikesPost(postId);

        assertEquals(2L, result);
    }

    @Test
    void testCountLikesPost_PostNotPublished() {
        post.setPublished(false);

        when(postRepository.existsById(postId)).thenReturn(true);
        when(postService.getPostById(postId)).thenReturn(post);

        assertThrows(IllegalArgumentException.class, () -> likeService.countLikesPost(postId));
    }

    @Test
    void testCountLikesPost_PostNotFound() {
        when(postRepository.existsById(postId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> likeService.countLikesPost(postId));
    }
}
