package faang.school.postservice.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentTest {

    @Test
    public void testIsNotVerifiedWithVerifiedComment() {
        // arrange
        Comment comment = Comment.builder()
                .verified(false)
                .build();
        boolean expected = true;

        // act
        boolean actual = comment.isNotVerified();

        // assert
        assertEquals(expected, actual);
    }

    @Test
    public void testIsNotVerifiedWithNotVerifiedComment() {
        // arrange
        Comment comment = Comment.builder()
                .verified(true)
                .build();
        boolean expected = false;

        // act
        boolean actual = comment.isNotVerified();

        // assert
        assertEquals(expected, actual);
    }
}
