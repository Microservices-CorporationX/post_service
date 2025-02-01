package faang.school.postservice.controller.news_feed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.news_feed_models.NewsFeedPost;
import faang.school.postservice.service.news_feed_service.NewsFeedService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(NewsFeedController.class)
class NewsFeedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NewsFeedService newsFeedService;

    @MockBean
    private UserContext userContext;

    @Test
    void getFeed_ShouldReturnFeedPosts() throws Exception {
        Long userId = 1L;
        Long lastViewedPostId = 100L;

        List<NewsFeedPost> mockPosts = List.of(
                NewsFeedPost.builder().postId(101L).content("Post 101").authorId(1L).build(),
                NewsFeedPost.builder().postId(102L).content("Post 102").authorId(2L).build()
        );

        when(userContext.getUserId()).thenReturn(userId);
        when(newsFeedService.getFeed(userId, lastViewedPostId)).thenReturn(mockPosts);

        mockMvc.perform(get("/api/v1/news_feed")
                        .param("lastViewedPostId", lastViewedPostId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(mockPosts.size()))
                .andExpect(jsonPath("$[0].postId").value(101L))
                .andExpect(jsonPath("$[0].content").value("Post 101"))
                .andExpect(jsonPath("$[1].postId").value(102L))
                .andExpect(jsonPath("$[1].content").value("Post 102"));

        verify(userContext).getUserId();
        verify(newsFeedService).getFeed(userId, lastViewedPostId);
    }

    @Test
    void getFeed_ShouldReturnFeedPosts_WhenNoLastViewedPostId() throws Exception {
        Long userId = 1L;

        List<NewsFeedPost> mockPosts = List.of(
                NewsFeedPost.builder().postId(103L).content("Post 103").authorId(1L).build()
        );

        when(userContext.getUserId()).thenReturn(userId);
        when(newsFeedService.getFeed(userId, null)).thenReturn(mockPosts);

        mockMvc.perform(get("/api/v1/news_feed")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(mockPosts.size()))
                .andExpect(jsonPath("$[0].postId").value(103L))
                .andExpect(jsonPath("$[0].content").value("Post 103"));

        verify(userContext).getUserId();
        verify(newsFeedService).getFeed(userId, null);
    }
}