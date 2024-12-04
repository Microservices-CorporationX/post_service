package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentMapperTest {
    private static CommentMapperImpl commentMapper;
    private final long authorId = 1L;
    private final long postId = 2L;
    private final long likeFirstId = 3L;
    private final long likeSecondId = 4L;
    private final long commentId = 5L;
    private final String content = "My Content";

    @BeforeAll
    public static void setUp() {
        commentMapper = new CommentMapperImpl();
    }

    @Test
    public void toEntitySuccessTest() {
        LocalDateTime now = LocalDateTime.now();

        List<Long> likeIds = List.of(
                likeFirstId,
                likeSecondId
        );

        CommentDto commentDto = new CommentDto();
        commentDto.setId(commentId);
        commentDto.setAuthorId(authorId);
        commentDto.setPostId(postId);
        commentDto.setContent(content);
        commentDto.setLikeIds(likeIds);
        commentDto.setCreatedAt(now);
        commentDto.setUpdatedAt(now);

        Comment comment = commentMapper.toEntity(commentDto);

        assertThat(comment).isNotNull();
        assertThat(comment.getId()).isEqualTo(commentDto.getId());
        assertThat(comment.getAuthorId()).isEqualTo(commentDto.getAuthorId());
        assertThat(comment.getPost().getId()).isEqualTo(commentDto.getPostId());
        assertThat(comment.getContent()).isEqualTo(comment.getContent());
        assertThat(comment.getLikes()).isNotNull();
        assertThat(comment.getLikes().size()).isEqualTo(commentDto.getLikeIds().size());
        for (int i = 0; i < comment.getLikes().size(); i++) {
            assertThat(comment.getLikes().get(0)).isNotNull();
            assertThat(comment.getLikes().get(i).getId()).isEqualTo(commentDto.getLikeIds().get(i));
        }
    }

    @Test
    public void toEntityWithNullFailTest() {
        Comment comment = commentMapper.toEntity(null);
        assertThat(comment).isNull();
    }

    @Test
    public void toDtoSuccessTest() {
        LocalDateTime now = LocalDateTime.now();

        Post post = getPost();

        List<Like> likes = List.of(
                Like.builder()
                        .id(likeFirstId)
                        .build(),
                Like.builder()
                        .id(likeSecondId)
                        .build()
        );

        Comment comment = Comment.builder()
                .id(commentId)
                .content(content)
                .authorId(authorId)
                .post(post)
                .likes(likes)
                .createdAt(now)
                .updatedAt(now)
                .build();

        CommentDto commentDto = commentMapper.toDto(comment);

        assertThat(commentDto).isNotNull();
        assertThat(commentDto.getId()).isEqualTo(comment.getId());
        assertThat(commentDto.getAuthorId()).isEqualTo(comment.getAuthorId());
        assertThat(commentDto.getPostId()).isEqualTo(comment.getPost().getId());
        assertThat(commentDto.getContent()).isEqualTo(comment.getContent());
        assertThat(commentDto.getCreatedAt()).isEqualTo(comment.getCreatedAt());
        assertThat(commentDto.getUpdatedAt()).isEqualTo(comment.getUpdatedAt());
        assertThat(commentDto.getLikeIds().size()).isEqualTo(comment.getLikes().size());
        for (int i = 0; i < commentDto.getLikeIds().size(); i++) {
            assertThat(commentDto.getLikeIds().get(i)).isNotNull();
            assertThat(commentDto.getLikeIds().get(i))
                    .isEqualTo(comment.getLikes().get(i).getId());
        }
    }

    @Test
    public void toDtoWithNullFailTest() {
        CommentDto commentDto = commentMapper.toDto(null);
        assertThat(commentDto).isNull();
    }

    @Test
    public void toDtoWithoutCommentIdFailTest() {
        Comment comment = Comment.builder().build();
        CommentDto commentDto = commentMapper.toDto(comment);

        assertThat(commentDto).isNotNull();
        assertThat(commentDto.getId()).isEqualTo(0L);
    }

    @Test
    public void toDtoWithoutPostOnlyFailTest() {
        Post post = getPost();

        Comment comment = Comment.builder()
                .id(commentId)
                .post(post)
                .build();

        CommentDto commentDto = commentMapper.toDto(comment);

        assertThat(commentDto).isNotNull();
        assertThat(commentDto.getPostId()).isEqualTo(comment.getPost().getId());
        assertThat(commentDto.getAuthorId()).isEqualTo(0L);
        assertThat(commentDto.getLikeIds().size()).isEqualTo(0);
        assertThat(commentDto.getContent()).isNull();
        assertThat(commentDto.getCreatedAt()).isNull();
        assertThat(commentDto.getUpdatedAt()).isNull();
    }

    @Test
    public void updateSuccessTest() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowOther = LocalDateTime.now();
        CommentDto commentDto = new CommentDto();
        commentDto.setId(100L);
        commentDto.setAuthorId(authorId);
        commentDto.setPostId(300L);
        commentDto.setContent(content);
        commentDto.setCreatedAt(now);
        commentDto.setUpdatedAt(now);

        Comment comment = Comment.builder()
                .id(commentId)
                .authorId(authorId)
                .post(getPost())
                .content("Other Content")
                .createdAt(nowOther)
                .updatedAt(nowOther)
                .build();

        commentMapper.update(commentDto, comment);

        assertThat(comment.getId()).isNotEqualTo(commentDto.getId());
        assertThat(comment.getAuthorId()).isEqualTo(commentDto.getAuthorId());
        assertThat(comment.getPost().getId()).isNotEqualTo(commentDto.getPostId());
        assertThat(comment.getContent()).isEqualTo(content);
        assertThat(comment.getLikes()).isNull();
    }

    private Post getPost() {
        return Post.builder()
                .id(postId)
                .build();
    }

    private Like getLikeFirst() {
        return Like.builder()
                .id(likeFirstId)
                .build();
    }

    private Like getLikeSecond() {
        return Like.builder()
                .id(likeSecondId)
                .build();
    }
}
