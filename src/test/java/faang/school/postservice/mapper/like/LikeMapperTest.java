package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LikeMapperTest {
    private final LikeMapper likeMapper = Mappers.getMapper(LikeMapper.class);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a");
    private final long likeId = 1L;
    private final long postId = 2L;
    private final long commentId = 3L;

    @Test
    public void toDtoSuccessTest() {
        LocalDateTime createdAt = LocalDateTime.now();

        Post post = getPost();
        Comment comment = getComment();

        Like like = Like.builder()
                .id(likeId)
                .post(post)
                .comment(comment)
                .createdAt(createdAt)
                .build();

        LikeDto likeDto = likeMapper.toDto(like);

        assertThat(likeDto).isNotNull();
        assertThat(likeDto.getId()).isEqualTo(like.getId());
        assertThat(likeDto.getPostId()).isEqualTo(like.getPost().getId());
        assertThat(likeDto.getCommentId()).isEqualTo(like.getComment().getId());
        assertThat(likeDto.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    public void toDtoWithoutPostFailTest() {
        Like like = Like.builder()
                .id(likeId)
                .build();

        LikeDto likeDto = likeMapper.toDto(like);
        assertThat(likeDto).isNotNull();
        assertThat(likeDto.getId()).isNotNull();
        assertThat(likeDto.getPostId()).isNull();
    }

    @Test
    public void toDtoWithoutCommentFailTest() {
        Post post = getPost();
        Like like = Like.builder()
                .id(likeId)
                .post(post)
                .build();

        LikeDto likeDto = likeMapper.toDto(like);
        assertThat(likeDto).isNotNull();
        assertThat(likeDto.getId()).isNotNull();
        assertThat(likeDto.getPostId()).isNotNull();
        assertThat(likeDto.getCommentId()).isNull();
    }

    @Test
    public void toDtoWithoutCreatedAtFailTest() {
        Post post = getPost();
        Comment comment = getComment();

        Like like = Like.builder()
                .id(likeId)
                .post(post)
                .comment(comment)
                .build();

        LikeDto likeDto = likeMapper.toDto(like);
        assertThat(likeDto).isNotNull();
        assertThat(likeDto.getId()).isNotNull();
        assertThat(likeDto.getCommentId()).isNotNull();
        assertThat(likeDto.getPostId()).isNotNull();
        assertThat(likeDto.getCreatedAt()).isNull();
    }

    @Test
    public void toEntitySuccessTest() {
        LocalDateTime createdAt = LocalDateTime.now();

        LikeDto likeDto = new LikeDto();
        likeDto.setPostId(postId);
        likeDto.setId(likeId);
        likeDto.setCommentId(commentId);
        likeDto.setCreatedAt(createdAt);

        Like like = likeMapper.toEntity(likeDto);

        assertThat(like).isNotNull();
        assertThat(like.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    public void toEntityWithoutCreatedAtFailTest() {
        LikeDto likeDto = new LikeDto();
        likeDto.setId(likeId);
        likeDto.setPostId(postId);
        likeDto.setCommentId(commentId);

        Like like = likeMapper.toEntity(likeDto);

        assertThat(like).isNotNull();
        assertThat(like.getCreatedAt()).isNull();
    }

    private Post getPost() {
        return Post.builder()
                .id(postId)
                .build();
    }

    private Comment getComment() {
        return Comment.builder()
                .id(commentId)
                .build();
    }
}
