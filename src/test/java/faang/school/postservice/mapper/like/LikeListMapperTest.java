package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LikeListMapperTest {
    private static LikeListMapperImpl likeListMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a");
    private final Long likeFirstId = 1L;
    private final Long likeSecondId = 2L;
    private final Long postFirstId = 3L;
    private final Long postSecondId = 4L;
    private final Long commentFirstId = 5L;
    private final Long commentSecondId = 6L;
    private final Long userFirstId = 5L;
    private final Long userSecondId = 6L;

    @BeforeAll
    public static void setUp() {
        LikeMapperImpl likeMapper = new LikeMapperImpl();
        likeListMapper = new LikeListMapperImpl(likeMapper);
    }

    @Test
    public void toLikeListSuccessTest() {
        LocalDateTime createdAt = LocalDateTime.now();

        List<LikeDto> likeDtoList = List.of(
                new LikeDto() {{
                    setId(likeFirstId);
                    setCommentId(commentFirstId);
                    setPostId(postFirstId);
                    setUserId(userFirstId);
                    setCreatedAt(createdAt);
                }},
                new LikeDto() {{
                    setId(likeSecondId);
                    setCommentId(commentSecondId);
                    setPostId(postSecondId);
                    setUserId(userSecondId);
                    setCreatedAt(createdAt);
                }}
        );
        List<Like> likeList = likeListMapper.toLikeList(likeDtoList);
        assertThat(likeList).isNotNull();
        for (int i = 0; i < likeDtoList.size(); i++) {
            assertThat(likeList.get(i)).isNotNull();
            assertThat(likeList.get(i).getUserId()).isEqualTo(likeDtoList.get(i).getUserId());
        }
    }

    @Test
    public void toLikeListWithNullFailTest() {
        List<Like> likeList = likeListMapper.toLikeList(null);
        assertThat(likeList).isNull();
    }

    @Test
    public void toLikeDtoListSuccessTest() {
        LocalDateTime now = LocalDateTime.now();

        Comment commentFirst = Comment.builder()
                .id(commentFirstId)
                .build();

        Post postFirst = Post.builder()
                .id(postFirstId)
                .build();

        Comment commentSecond = Comment.builder()
                .id(commentFirstId)
                .build();

        Post postSecond = Post.builder()
                .id(postFirstId)
                .build();

        List<Like> likeList = List.of(
                Like.builder()
                        .id(likeFirstId)
                        .post(postFirst)
                        .comment(commentFirst)
                        .userId(userFirstId)
                        .createdAt(now)
                        .build(),
                Like.builder()
                        .id(likeSecondId)
                        .post(postSecond)
                        .comment(commentSecond)
                        .userId(userSecondId)
                        .createdAt(now)
                        .build()
        );

        List<LikeDto> likeDtoList = likeListMapper.toLikeDtoList(likeList);

        for (int i = 0; i < likeDtoList.size(); i++) {
            assertThat(likeDtoList.get(i)).isNotNull();
            assertThat(likeDtoList.get(i).getId()).isEqualTo(likeList.get(i).getId());
            assertThat(likeDtoList.get(i).getPostId()).isEqualTo(likeList.get(i).getPost().getId());
            assertThat(likeDtoList.get(i).getCommentId()).isEqualTo(likeList.get(i).getComment().getId());
            assertThat(likeDtoList.get(i).getUserId()).isEqualTo(likeList.get(i).getUserId());
            assertThat(likeDtoList.get(i).getCreatedAt()).isEqualTo(now);
        }
    }

    @Test
    public void toLikeDtoListFailTest() {
        List<LikeDto> likeDtoList = likeListMapper.toLikeDtoList(null);
        assertThat(likeDtoList).isNull();
    }
}
