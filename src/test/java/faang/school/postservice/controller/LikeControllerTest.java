package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
@ContextConfiguration(classes = {LikeController.class, LikeService.class})
public class LikeControllerTest {
    private final static String POST_URL = "/posts/{postId}/likers";

    private final static String COMMENT_URL = "/comments/{commentId}/likers";

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LikeService likeService;

    @Test
    void testGetUsersWhoLikePostByPostId() throws Exception {
        long postId = 1L;

        List<UserDto> userDtos = List.of(
                UserDto.builder().id(1L).username("Name1").email("test1@gmail.com").build(),
                UserDto.builder().id(2L).username("Name2").email("test2@gmail.com").build()
        );

        when(likeService.getUsersWhoLikePostByPostId(postId)).thenReturn(userDtos);

        mockMvc.perform(get(POST_URL, postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(userDtos.size()))
                .andExpect(jsonPath("$[0].id").value(userDtos.get(0).getId()))
                .andExpect(jsonPath("$[0].username").value(userDtos.get(0).getUsername()))
                .andExpect(jsonPath("$[0].email").value(userDtos.get(0).getEmail()))
                .andExpect(jsonPath("$[1].id").value(userDtos.get(1).getId()))
                .andExpect(jsonPath("$[1].username").value(userDtos.get(1).getUsername()))
                .andExpect(jsonPath("$[1].email").value(userDtos.get(1).getEmail()));

        verify(likeService, times(1)).getUsersWhoLikePostByPostId(postId);
    }

    @Test
    void testGetUsersWhoLikeComments() throws Exception {
        long commentId = 1L;

        List<UserDto> userDtos = List.of(
                UserDto.builder()
                        .id(1L)
                        .username("JohnDoe")
                        .email("johndoe@example.com")
                        .build()
        );

        when(likeService.getUsersWhoLikeComments(commentId)).thenReturn(userDtos);

        mockMvc.perform(get(COMMENT_URL, commentId))
                .andExpect(status().isOk())
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(userDtos)));

        verify(likeService, times(1)).getUsersWhoLikeComments(commentId);
    }

    @Test
    void addLikeToPostShouldReturnCreatedResponse() throws Exception {
        LikeResponseDto likeResponseDto = LikeResponseDto.builder()
                .id(10L)
                .userId(2L)
                .postId(1L)
                .commentId(3L)
                .build();

        LikeDto likeDto = LikeDto.builder()
                .userId(2L)
                .commentId(3L)
                .build();

        when(likeService.addLikeToPost(1L, likeDto))
                .thenReturn(likeResponseDto);

        mockMvc.perform(post("/posts/1/likes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "postId": 1,
                                  "userId": 2,
                                  "commentId": 3
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.userId").value(2))
                .andExpect(jsonPath("$.postId").value(1))
                .andExpect(jsonPath("$.commentId").value(3));

        verify(likeService, times(1)).addLikeToPost(1L, likeDto);
    }
}
