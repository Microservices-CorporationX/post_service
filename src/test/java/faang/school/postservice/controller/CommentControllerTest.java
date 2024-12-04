package faang.school.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.utilities.UrlUtils;
import faang.school.postservice.validator.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {
    private final static String mainUrl = UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.COMMENT;

    private MockMvc mockMvc;
    @Mock
    private CommentService commentService;

    @Mock
    private CommentValidator commentValidator;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final long authorIdFirst = 5;
    private final long authorIdSecond = 5;
    private final long commentIdFirst = 1;
    private final long commentIdSecond = 2;
    private final long postId = 3;
    private final String content = "Content";

    @Test
    public void createCommentSuccessTest() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(commentIdFirst);
        commentDto.setContent(content);
        commentDto.setAuthorId(authorIdFirst);
        commentDto.setPostId(postId);

        CommentDto commentDtoExpected = new CommentDto();
        commentDtoExpected.setId(commentIdSecond);
        commentDtoExpected.setContent(content);
        commentDtoExpected.setAuthorId(authorIdSecond);
        commentDtoExpected.setPostId(postId);

        when(commentService.createComment(commentDto)).thenReturn(commentDtoExpected);

        mockMvc.perform(post(mainUrl)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(commentIdSecond))
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.authorId").value(authorIdSecond))
                .andExpect(jsonPath("$.postId").value(postId));

        verify(commentService, times(1)).createComment(commentDto);
    }

    @Test
    public void updateCommentSuccessTest() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(commentIdFirst);
        commentDto.setContent(content);
        commentDto.setAuthorId(authorIdFirst);
        commentDto.setPostId(postId);

        CommentDto commentDtoExpected = new CommentDto();
        commentDtoExpected.setId(commentIdSecond);
        commentDtoExpected.setContent(content);
        commentDtoExpected.setAuthorId(authorIdSecond);
        commentDtoExpected.setPostId(postId);

        when(commentService.updateComment(commentDto)).thenReturn(commentDtoExpected);

        mockMvc.perform(put(mainUrl)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(commentIdSecond))
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.authorId").value(authorIdSecond))
                .andExpect(jsonPath("$.postId").value(postId));

        verify(commentService, times(1)).updateComment(commentDto);
    }

    @Test
    public void getAllCommentsSuccessTest() throws Exception {
        CommentDto commentDtoFirst = new CommentDto();
        commentDtoFirst.setId(commentIdFirst);
        CommentDto commentDtoSecond = new CommentDto();
        commentDtoSecond.setId(commentIdSecond);

        List<CommentDto> expectedComments = List.of(
                commentDtoFirst,
                commentDtoSecond
        );

        when(commentService.getAllComments(postId)).thenReturn(expectedComments);

        mockMvc.perform(get(mainUrl + "/" + postId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[1].id", is(2)));
    }

    @Test
    public void getAllCommentsWithEmptyResultSuccessTest() throws Exception {
        when(commentService.getAllComments(postId)).thenReturn(new ArrayList<>());

        mockMvc.perform(get(mainUrl + "/" + postId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void deleteCommentSuccessTest() throws Exception {
        doNothing().when(commentService).deleteComment(commentIdFirst);

        mockMvc.perform(delete(mainUrl + "/" + commentIdFirst))
                .andExpect(status().isOk());

        verify(commentService, times(1)).deleteComment(commentIdFirst);
    }
}
