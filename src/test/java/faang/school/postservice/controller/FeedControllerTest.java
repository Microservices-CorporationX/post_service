package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.FeedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FeedControllerTest {
    private List<PostDto> dtoList;
    private MockMvc mockMvc;
    @Mock
    private FeedService service;
    @Mock
    private UserContext userContext;
    @InjectMocks
    private FeedController controller;

    @BeforeEach
    public void setUp() {
        //Arrange
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        dtoList = List.of(new PostDto());
    }

    @Test
    public void testFeedServiceGetUserFeed() throws Exception {
        //Act
        when(service.getFeedByUserId(2L, 1L)).thenReturn(dtoList);
        when(userContext.getUserId()).thenReturn(2L);
        //Assert
        mockMvc.perform(get("/feed?postId=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testFeedServiceGetUserFeedWhenPostIdNull() throws Exception {
        //Act
        when(service.getFeedByUserId(2L)).thenReturn(dtoList);
        when(userContext.getUserId()).thenReturn(2L);
        //Assert
        mockMvc.perform(get("/feed")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testFeedServiceSendHeatEventsAsync() throws Exception {
        //Assert
        mockMvc.perform(get("/heat")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}