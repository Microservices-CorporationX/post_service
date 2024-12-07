package faang.school.postservice.mapper;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LikeMapperTest {

    private final LikeMapper likeMapper = Mappers.getMapper(LikeMapper.class);

    @Test
    public void testToEntity_whenValidLikeDto_thenReturnLike() {
        // Arrange
        LikeDto likeDto = new LikeDto();
        likeDto.setUserId(1L);

        // Act
        Like like = likeMapper.toEntity(likeDto);

        // Assert
        assertEquals(likeDto.getUserId(), like.getUserId());
        assertNull(like.getPost());
        assertNull(like.getComment());
    }

    @Test
    // todo: how to check validation constraints?
    public void testToEntity_whenLikeDtoWithoutUserId_thenReturnLikeWithNullFields() {
        // Arrange
        LikeDto likeDto = new LikeDto(); // userId is not set

        // Act
        Like like = likeMapper.toEntity(likeDto);

        // Assert
        assertNull(like.getUserId());
        assertNull(like.getPost());
        assertNull(like.getComment());
    }

    @Test
    public void testToDto_whenValidLike_thenReturnLikeDto() {
        // Arrange
        Post post = new Post();
        post.setId(1L);

        Comment comment = new Comment();
        comment.setId(2L);

        Like like = new Like();
        like.setId(3L);
        like.setUserId(4L);
        like.setPost(post);
        like.setComment(comment);
        like.setCreatedAt(LocalDateTime.now());

        // Act
        LikeDto likeDto = likeMapper.toDto(like);

        // Assert
        assertEquals(like.getId(), likeDto.getId());
        assertEquals(like.getUserId(), likeDto.getUserId());
        assertEquals(like.getPost().getId(), likeDto.getPostId());
        assertEquals(like.getComment().getId(), likeDto.getCommentId());
        assertEquals(like.getCreatedAt(), likeDto.getCreatedAt());
    }

    @Test
    public void testToDto_whenLikeWithoutPost_thenReturnLikeDtoWithoutPostId() {
        // Arrange
        Comment comment = new Comment();
        comment.setId(2L);

        Like like = new Like();
        like.setId(3L);
        like.setUserId(4L);
        like.setComment(comment);
        like.setCreatedAt(LocalDateTime.now());

        // Act
        LikeDto likeDto = likeMapper.toDto(like);

        // Assert
        assertEquals(like.getId(), likeDto.getId());
        assertEquals(like.getUserId(), likeDto.getUserId());
        assertNull(likeDto.getPostId());
        assertEquals(like.getComment().getId(), likeDto.getCommentId());
        assertEquals(like.getCreatedAt(), likeDto.getCreatedAt());
    }

    @Test
    public void testToDto_whenLikeWithoutComment_thenReturnLikeDtoWithoutCommentId() {
        // Arrange
        Post post = new Post();
        post.setId(1L);

        Like like = new Like();
        like.setId(3L);
        like.setUserId(4L);
        like.setPost(post);
        like.setCreatedAt(LocalDateTime.now());

        // Act
        LikeDto likeDto = likeMapper.toDto(like);

        // Assert
        assertEquals(like.getId(), likeDto.getId());
        assertEquals(like.getUserId(), likeDto.getUserId());
        assertEquals(like.getPost().getId(), likeDto.getPostId());
        assertNull(likeDto.getCommentId());
        assertEquals(like.getCreatedAt(), likeDto.getCreatedAt());
    }
}