package faang.school.postservice.model.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PostCacheTest {

    private static final int MAX_COMMENTS = 3;

    private PostCache postCache;

    @BeforeEach
    public void setUp() {
        postCache = PostCache.builder()
                .likeCount(0L)
                .viewCount(0L)
                .build();
    }

    @Test
    public void testAddComment() {
        // arrange
        TreeSet<CommentCache> comments = new TreeSet<>();
        CommentCache firstComment = CommentCache.builder()
                .id(1L)
                .build();
        CommentCache secondComment = CommentCache.builder()
                .id(2L)
                .build();
        CommentCache thirdComment = CommentCache.builder()
                .id(3L)
                .build();
        comments.add(firstComment);
        comments.add(secondComment);
        postCache.setComments(comments);

        TreeSet<CommentCache> expected = new TreeSet<>();
        expected.add(firstComment);
        expected.add(secondComment);
        expected.add(thirdComment);

        // act
        postCache.addComment(thirdComment, MAX_COMMENTS);
        TreeSet<CommentCache> result = postCache.getComments();

        // assert
        assertEquals(expected, result);
    }

    @Test
    public void testAddCommentRemovesExcessComments() {
        TreeSet<CommentCache> comments = new TreeSet<>();
        CommentCache firstComment = CommentCache.builder()
                .id(1L)
                .build();
        CommentCache secondComment = CommentCache.builder()
                .id(2L)
                .build();
        CommentCache thirdComment = CommentCache.builder()
                .id(3L)
                .build();
        CommentCache fourthComment = CommentCache.builder()
                .id(4L)
                .build();
        comments.add(firstComment);
        comments.add(secondComment);
        comments.add(thirdComment);
        postCache.setComments(comments);

        TreeSet<CommentCache> expected = new TreeSet<>();
        expected.add(secondComment);
        expected.add(thirdComment);
        expected.add(fourthComment);

        // act
        postCache.addComment(fourthComment, MAX_COMMENTS);
        TreeSet<CommentCache> result = postCache.getComments();

        // assert
        assertEquals(expected, result);
    }

    @Test
    public void testIncrementLikeCount() {
        // arrange
        int expectedLikeCount = 1;

        // act
        postCache.incrementLikeCount();

        // assert
        assertEquals(expectedLikeCount, postCache.getLikeCount());
    }

    @Test
    public void testIncrementViewCount() {
        // arrange
        int expectedViewCount = 1;

        // act
        postCache.incrementViewCount();

        // assert
        assertEquals(expectedViewCount, postCache.getViewCount());
    }
}
