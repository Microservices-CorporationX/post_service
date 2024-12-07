package faang.school.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class LikeControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private LikeService likeService;

    @InjectMocks
    private LikeController likeController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(likeController).build();
    }

    @Test
    public void testLikePost_Success() throws Exception {
        long postId = 1L;

        LikeDto likeDto = new LikeDto();
        likeDto.setUserId(1L);

        LikeDto expectedResponse = new LikeDto();
        expectedResponse.setUserId(1L);

        String likeDtoJson = objectMapper.writeValueAsString(likeDto);
        String expectedResponseJson = objectMapper.writeValueAsString(expectedResponse);

        when(likeService.likePost(ArgumentMatchers.eq(postId), ArgumentMatchers.any(LikeDto.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post-service/v1/posts/{postId}/like", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(likeDtoJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponseJson));
    }

    @Test
    public void testLikePost_MissingUserId() throws Exception {
        long postId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post-service/v1/posts/{postId}/like", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testLikePost_InvalidUserId() throws Exception {
        long postId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post-service/v1/posts/{postId}/like", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": 0}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void testUnlikePost_Success() throws Exception {
        long postId = 1L;
        LikeDto likeDto = new LikeDto();
        likeDto.setUserId(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/post-service/v1/posts/{postId}/like", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": 1}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testUnlikePost_MissingUserId() throws Exception {
        long postId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/post-service/v1/posts/{postId}/like", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUnlikePost_InvalidUserId() throws Exception {
        long postId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/post-service/v1/posts/{postId}/like", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": 0}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}