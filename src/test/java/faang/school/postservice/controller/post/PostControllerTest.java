package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.PostValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {
    @Mock
    private PostService postService;
    @Mock
    private PostValidator postValidator;
    @InjectMocks
    private PostController postController;


    @Test
    void createDraftPostWithNoAuthorTest() {
        PostDto postDto = new PostDto();
        Mockito.doThrow(new DataValidationException("У поста должен быть автор")).when(postValidator).validateAuthorCount(postDto);
        assertThrows(DataValidationException.class, () -> postController.createDraftPost(postDto));
    }

    @Test
    void createDraftPostWithTwoAuthorsTest() {
        PostDto postDto = new PostDto();
        postDto.setAuthorId(1L);
        postDto.setProjectId(1L);
        Mockito.doThrow(new DataValidationException("У поста должен быть только один автор")).when(postValidator).validateAuthorCount(postDto);
        assertThrows(DataValidationException.class, () -> postController.createDraftPost(postDto));
    }

    @Test
    void createDraftPostWithEmptyContent() {
        PostDto postDto = new PostDto();
        postDto.setAuthorId(1L);
        Mockito.doThrow(new DataValidationException("Пост не может быть пустым")).when(postValidator).validateContentExists(postDto);
        assertThrows(DataValidationException.class, () -> postController.createDraftPost(postDto));

        postDto.setContent("");
        assertThrows(DataValidationException.class, () -> postController.createDraftPost(postDto));
    }

    @Test
    void shouldCreateDraftPost() {
        PostDto postDto = new PostDto();
        postDto.setAuthorId(1L);
        postDto.setContent("test");
        postController.createDraftPost(postDto);
        Mockito.verify(postService, Mockito.times(1)).createDraftPost(postDto);
    }

    @Test
    void updatePostWithEmptyContentTest() {
        UpdatePostDto postDto = new UpdatePostDto();
        postDto.setContent("");
        doThrow(new DataValidationException("Пост не может быть пустым"))
                .when(postValidator).validateContentExists(postDto);
        assertThrows(DataValidationException.class,
                () -> postController.updatePost(postDto, 1L));
    }

    @Test
    void updateCorrectPostTest() {
        UpdatePostDto postDto = new UpdatePostDto();
        postDto.setContent("test");
        postController.updatePost(postDto, 1L);
        Mockito.verify(postService, Mockito.times(1)).updatePost(postDto, 1L);
    }

    @Test
    void shouldDeletePost() {
        postController.deletePost(1L);
        verify(postService, times(1)).deletePost(1L);
    }
}