package faang.school.postservice.model.cache;

import org.bouncycastle.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FeedTest {

    private static final int MAX_FEED_SIZE = 20;
    private static final int NUM_POSTS = 20;

    private Feed feed;

    @BeforeEach
    public void setUp() {
        long userId = 5L;
        feed = new Feed(userId);
    }

    @Test
    public void testAddPostToFeed() {
        // arrange
        long firstPostId = 1L;
        long secondPostId = 2L;
        long thirdPostId = 3L;
        FeedPost firstPost = new FeedPost(firstPostId);
        FeedPost secondPost = new FeedPost(secondPostId);
        FeedPost thirdPost = new FeedPost(thirdPostId);
        TreeSet<FeedPost> expected = new TreeSet<>();
        expected.add(firstPost);
        expected.add(secondPost);
        expected.add(thirdPost);

        // act
        feed.addPostToFeed(firstPostId, MAX_FEED_SIZE);
        feed.addPostToFeed(secondPostId, MAX_FEED_SIZE);
        feed.addPostToFeed(thirdPostId, MAX_FEED_SIZE);
        TreeSet<FeedPost> result = feed.getFeed();

        // assert
        assertEquals(expected, result);
    }

    @Test
    public void testAddPostToFeedCreatesNewFeedIfNull() {
        // arrange
        feed.setFeed(null);
        long postId = 5L;

        // act
        feed.addPostToFeed(postId, MAX_FEED_SIZE);

        // assert
        assertNotNull(feed.getFeed());
    }

    @Test
    public void testGetLastNPosts() {
        // arrange
        List<Long> expected = new ArrayList<>();
        for (long i = 1; i < 10; i++) {
            expected.add(i);
            feed.addPostToFeed(i, MAX_FEED_SIZE);
        }
        Collections.reverse(expected);

        // act
        List<Long> postIds = feed.getLastNPosts(NUM_POSTS, null);

        // assert
        assertEquals(expected, postIds);
    }

    @Test
    public void testGetLastNPostsFiltersByLastPostId() {
        // arrange
        long lastPostId = 4L;
        for (long i = 1; i < 10; i++) {
            feed.addPostToFeed(i, MAX_FEED_SIZE);
        }
        List<Long> expected = List.of(3L, 2L, 1L);

        // act
        List<Long> postIds = feed.getLastNPosts(NUM_POSTS, lastPostId);

        // assert
        assertEquals(expected, postIds);
    }

    @Test
    public void testGetLastNPostsRemovesExcess() {
        // arrange
        int numPosts = MAX_FEED_SIZE;
        long expectedSize = MAX_FEED_SIZE;
        for (long i = 1; i < 30; i++) {
            feed.addPostToFeed(i, MAX_FEED_SIZE);
        }

        // act
        List<Long> postIds = feed.getLastNPosts(numPosts, null);

        // assert
        assertEquals(expectedSize, postIds.size());
    }
}
