package faang.school.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.handler.ExceptionApiHandler;
import faang.school.postservice.service.PostResourceService;
import faang.school.postservice.service.PostService;
import faang.school.postservice.utilities.UrlUtils;
import jakarta.persistence.EntityNotFoundException;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@Import({PostController.class, ExceptionApiHandler.class})
class PostControllerTest {

    private final static String mainUrl = UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.POSTS;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @MockBean
    private PostService postService;
    @MockBean
    private PostResourceService postResourceService;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserContext userContext;
    private MockMultipartFile validImage;
    private ResourceDto resourceDto;

    @BeforeEach
    void setUp() {
        validImage = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                new byte[]{1, 2, 3, 4}
        );
        resourceDto = new ResourceDto("Image_key",
                1L,
                LocalDateTime.now(),
                "image",
                "image/jpeg",
                1L);
    }

    @Test
    void createDraftPostByUserSuccessTest() throws Exception {
        PostDto postDto = new PostDto(1L, "Test content", 1L, null, null, 1);
        Long expectedPostId = 1L;
        when(postService.createDraftPost(postDto)).thenReturn(1L);

        mockMvc.perform(post(mainUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(expectedPostId));

        verify(postService).createDraftPost(postDto);
    }

    @Test
    void createDraftPostByProjectSuccessTest() throws Exception {
        PostDto postDto = new PostDto(1L, "Test content", null, 3L, null, 1);
        Long expectedPostId = 2L;
        when(postService.createDraftPost(postDto)).thenReturn(2L);

        mockMvc.perform(post(mainUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(expectedPostId));

        verify(postService).createDraftPost(postDto);
    }

    @Test
    void createDraftPostByUserContentIsBlankFailTest() throws Exception {
        PostDto postDto = new PostDto(1L, "  ", 1L, null, null, 1);

        mockMvc.perform(post(mainUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CoreMatchers.containsString("Content should not be blank")));
    }

    @Test
    void createDraftPostByUserContentNullFailTest() throws Exception {
        PostDto postDto = new PostDto(1L, null, 1L, null, null, 1);

        mockMvc.perform(post(mainUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CoreMatchers.containsString("Content should not be blank")));
    }

    @Test
    void createDraftPostByUserIdNullAndProjectIdNullFailTest() throws Exception {
        PostDto postDto = new PostDto(1L, "Test for test", null, null, null, 1);

        mockMvc.perform(post(mainUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{'message':'idProject or idUser are NULL'}"));
    }

    @Test
    void createDraftPostByUserIdAndProjectIdFailTest() throws Exception {
        PostDto postDto = new PostDto(1L, "Test for test", 1L, 2L, null, 1);

        mockMvc.perform(post(mainUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{'message':'idProject or idUser both has value'}"));
    }

    @Test
    void publishPostSuccessTest() throws Exception {
        PostDto postDto = new PostDto(1L, "Test content", 1L, null, null, 1);
        Long postId = 1L;

        when(postService.publishPost(postId)).thenReturn(postDto);
        mockMvc.perform(patch(mainUrl + UrlUtils.ID, postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Test content"))
                .andExpect(jsonPath("$.userId").value(1L));

        verify(postService).publishPost(postId);
    }

    @Test
    void publishPostIsPublishedFailTest() throws Exception {
        Long postId = 1L;
        String errorMessage = "Post already published, id: " + postId;

        when(postService.publishPost(anyLong())).thenThrow(new IllegalArgumentException(errorMessage));
        mockMvc.perform(patch(mainUrl + UrlUtils.ID, postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    void publishPostNotFoundFailTest() throws Exception {
        long invalidPostId = 999L;
        String errorMessage = "Post not found with ID: " + invalidPostId;

        when(postService.publishPost(anyLong())).thenThrow(new EntityNotFoundException(errorMessage));
        mockMvc.perform(patch(mainUrl + UrlUtils.ID, 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    void deletePostSuccessTest() throws Exception {
        Long postId = 1L;
        when(postService.deletePost(postId)).thenReturn(postId);

        mockMvc.perform(delete(mainUrl + UrlUtils.ID, postId))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(postId)));

        verify(postService).deletePost(postId);
    }

    @Test
    void deletePostIsDeletedFailTest() throws Exception {
        Long postId = 1L;
        String errorMessage = "Post with id: " + postId + " was deleted";

        when(postService.deletePost(anyLong())).thenThrow(new IllegalArgumentException("Post with id: " + postId + " was deleted"));
        mockMvc.perform(delete(mainUrl + UrlUtils.ID, postId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(postService).deletePost(postId);
    }

    @Test
    void getPostSuccessTest() throws Exception {
        Long postId = 1L;

        PostDto postDto = new PostDto(1L, "Sample Post", 1L, 2L, null, 1);
        when(postService.getPost(anyLong())).thenReturn(postDto);

        mockMvc.perform(get(mainUrl + "/" + postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Sample Post"))
                .andExpect(jsonPath("$.userId").value(1L));

        verify(postService).getPost(1L);
    }

    @Test
    void getPostNotFoundFailTest() throws Exception {
        long invalidPostId = 999L;
        String errorMessage = "Post not found with ID: " + invalidPostId;

        when(postService.publishPost(anyLong())).thenThrow(new EntityNotFoundException(errorMessage));
        mockMvc.perform(patch(mainUrl + UrlUtils.ID, 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    void getDraftPostsByUserIdSuccessTest() throws Exception {
        Long userId = 1L;
        List<PostDto> drafts = List.of(
                new PostDto(1L, "Draft 1", 1L, null, null, 1),
                new PostDto(2L, "Draft 2", 2L, null, null, 1)
        );
        when(postService.getDraftPostsForUser(userId)).thenReturn(drafts);

        mockMvc.perform(get(mainUrl + UrlUtils.USER + UrlUtils.DRAFT, userId)
                        .param("idUser", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(drafts.size()))
                .andExpect(jsonPath("$[0].userId").value(1L))
                .andExpect(jsonPath("$[0].content").value("Draft 1"))
                .andExpect(jsonPath("$[1].userId").value(2L))
                .andExpect(jsonPath("$[1].content").value("Draft 2"));

        verify(postService).getDraftPostsForUser(userId);
    }

    @Test
    void getDraftPostsByUserBadUserIdFailTest() throws Exception {
        mockMvc.perform(get(mainUrl + UrlUtils.USER + UrlUtils.DRAFT, 0)
                        .param("idUser", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDraftPostsByUserIdUserNotExistFailTest() throws Exception {
        Long userId = 99L;
        String errorMessage = "User id: " + userId + " not found";

        when(postService.getDraftPostsForUser(anyLong())).thenThrow(new IllegalArgumentException(errorMessage));
        mockMvc.perform(get(mainUrl + UrlUtils.USER + UrlUtils.DRAFT, userId)
                        .param("idUser", String.valueOf(userId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    void getDraftPostsByProjectIdSuccessTest() throws Exception {
        Long projectId = 1L;
        List<PostDto> drafts = List.of(
                new PostDto(1L, "Draft 1", null, 1L, null, 1),
                new PostDto(2L, "Draft 2", null, 2L, null, 1)
        );
        when(postService.getDraftPostsForProject(projectId)).thenReturn(drafts);

        mockMvc.perform(get(mainUrl + UrlUtils.PROJECT + UrlUtils.DRAFT, projectId)
                        .param("idProject", String.valueOf(projectId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(drafts.size()))
                .andExpect(jsonPath("$[0].projectId").value(1L))
                .andExpect(jsonPath("$[0].content").value("Draft 1"))
                .andExpect(jsonPath("$[1].projectId").value(2L))
                .andExpect(jsonPath("$[1].content").value("Draft 2"));

        verify(postService).getDraftPostsForProject(projectId);
    }

    @Test
    void getDraftPostsByProjectBadProjectIdFailTest() throws Exception {
        mockMvc.perform(get(mainUrl + UrlUtils.PROJECT + UrlUtils.DRAFT, 0)
                        .param("idProject", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDraftPostsByProjectIdProjectNotExistFailTest() throws Exception {
        Long projectId = 99L;
        String errorMessage = "Project id: " + projectId + " not found";

        when(postService.getDraftPostsForProject(anyLong())).thenThrow(new IllegalArgumentException(errorMessage));
        mockMvc.perform(get(mainUrl + UrlUtils.PROJECT + UrlUtils.DRAFT, projectId)
                        .param("idProject", String.valueOf(projectId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    void getPublishedPostsByUserIdSuccessTest() throws Exception {
        Long userId = 1L;
        List<PostDto> drafts = List.of(
                new PostDto(1L, "Publish 1", 1L, null, null, 1),
                new PostDto(2L, "Publish 2", 2L, null, null, 1)
        );
        when(postService.getPublishedPostsForUser(userId)).thenReturn(drafts);

        mockMvc.perform(get(mainUrl + UrlUtils.USER + UrlUtils.PUBLISHED, userId)
                        .param("idUser", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(drafts.size()))
                .andExpect(jsonPath("$[0].userId").value(1L))
                .andExpect(jsonPath("$[0].content").value("Publish 1"))
                .andExpect(jsonPath("$[1].userId").value(2L))
                .andExpect(jsonPath("$[1].content").value("Publish 2"));

        verify(postService).getPublishedPostsForUser(userId);
    }

    @Test
    void getPublishedPostsByUserBadUserIdFailTest() throws Exception {
        mockMvc.perform(get(mainUrl + UrlUtils.USER + UrlUtils.PUBLISHED, 0)
                        .param("idUser", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPublishedPostsByUserIdUserNotExistFailTest() throws Exception {
        Long userId = 99L;
        String errorMessage = "User id: " + userId + " not found";

        when(postService.getPublishedPostsForUser(anyLong())).thenThrow(new IllegalArgumentException(errorMessage));
        mockMvc.perform(get(mainUrl + UrlUtils.USER + UrlUtils.PUBLISHED, userId)
                        .param("idUser", String.valueOf(userId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    void getPublishedPostsByProjectIdSuccessTest() throws Exception {
        Long projectId = 1L;
        List<PostDto> publishedPosts = List.of(
                new PostDto(1L, "Post 1", null, 1L, null, 1),
                new PostDto(2L, "Post 2", null, 2L, null, 1)
        );
        when(postService.getPublishedPostForProject(projectId)).thenReturn(publishedPosts);

        mockMvc.perform(get(mainUrl + UrlUtils.PROJECT + UrlUtils.PUBLISHED, projectId)
                        .param("idProject", String.valueOf(projectId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(publishedPosts.size()))
                .andExpect(jsonPath("$[0].projectId").value(1L))
                .andExpect(jsonPath("$[0].content").value("Post 1"))
                .andExpect(jsonPath("$[1].projectId").value(2L))
                .andExpect(jsonPath("$[1].content").value("Post 2"));

        verify(postService).getPublishedPostForProject(projectId);
    }

    @Test
    void getPublishedPostsByProjectBadProjectIdFailTest() throws Exception {
        mockMvc.perform(get(mainUrl + UrlUtils.PROJECT + UrlUtils.PUBLISHED, 0)
                        .param("idProject", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPublishedPostsByProjectIdProjectNotExistFailTest() throws Exception {
        Long projectId = 99L;
        String errorMessage = "Project id: " + projectId + " not found";

        when(postService.getPublishedPostForProject(anyLong())).thenThrow(new IllegalArgumentException(errorMessage));
        mockMvc.perform(get(mainUrl + UrlUtils.PROJECT + UrlUtils.PUBLISHED, projectId)
                        .param("idProject", String.valueOf(projectId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    void addImageSuccessTest() throws Exception {
        Long postId = 1L;
        Mockito.when(postResourceService.addPostImage(eq(postId), any())).thenReturn(resourceDto);

        mockMvc.perform(multipart(mainUrl + UrlUtils.ID + UrlUtils.IMAGE, postId)
                        .file(validImage)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value(resourceDto.key()))
                .andExpect(jsonPath("$.name").value(resourceDto.name()));
    }

    @Test
    void addImageImageIsNullFailTest() throws Exception {
        Long postId = 1L;
        mockMvc.perform(multipart(mainUrl + UrlUtils.ID + UrlUtils.IMAGE, postId)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteImageByKeySuccessTest() throws Exception {
        String key = "valid-key";
        Long expectedId = 1L;

        when(postResourceService.deleteImageByKey(key)).thenReturn(expectedId);

        mockMvc.perform(delete(mainUrl)
                        .param("key", key))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(expectedId));
    }

    @Test
    void deleteImageByKeyWhenKeyIsBlankFailTest() throws Exception {
        mockMvc.perform(delete(mainUrl)
                        .param("key", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteImageByKeyResourceNotFoundFailTest() throws Exception {
        String key = "nonexistent-key";
        String errorMessage = "Resource with key '" + key + "' not found";

        when(postResourceService.deleteImageByKey(key))
                .thenThrow(new EntityNotFoundException(errorMessage));

        mockMvc.perform(delete(mainUrl)
                        .param("key", key))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    void getImageByKey_ShouldReturnImage_WhenKeyExists() throws Exception {
        Long postId = 1L;
        String key = "valid-key";
        byte[] imageData = "fakeImageData".getBytes();

        when(postResourceService.getImageByKey(key)).thenReturn(imageData);


        mockMvc.perform(get(mainUrl + UrlUtils.ID + UrlUtils.IMAGE, postId)
                        .param("key", key))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE))
                .andExpect(content().bytes(imageData));
    }

    @Test
    void getImageByKeyWhenKeyIsBlankFailTest() throws Exception {
        Long postId = 1L;
        String key = "";

        mockMvc.perform(get(mainUrl + UrlUtils.ID + UrlUtils.IMAGE, postId)
                        .param("key", key))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getImagesByKeyWhenPostIdIsInvalidFailTest() throws Exception {
        Long postId = 0L;
        String key = "valid-key";

        mockMvc.perform(get(mainUrl + UrlUtils.ID + UrlUtils.IMAGE, postId)
                        .param("key", key))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllImagesByPostIdSuccessTest() throws Exception {
        Long postId = 1L;
        byte[] image1 = "image1Data".getBytes();
        byte[] image2 = "image2Data".getBytes();
        List<byte[]> images = List.of(image1, image2);

        when(postResourceService.getAllImagesByPostId(postId)).thenReturn(images);

        mockMvc.perform(get(mainUrl + UrlUtils.ID + UrlUtils.IMAGE + UrlUtils.ALL, postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(images.size()));
    }


    @Test
    void getAllImagesByPostIdWhenPostIdIsInvalidFailTest() throws Exception {
        Long postId = 0L;

        mockMvc.perform(get(mainUrl + UrlUtils.ID + UrlUtils.IMAGE + UrlUtils.ALL, postId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllImagesByPostIdWhenNoImagesExistFailTest() throws Exception {
        Long postId = 1L;

        when(postResourceService.getAllImagesByPostId(postId))
                .thenThrow(new EntityNotFoundException("Image for id Post " + postId + " not found"));

        mockMvc.perform(get(mainUrl + UrlUtils.ID + UrlUtils.IMAGE + UrlUtils.ALL, postId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Image for id Post " + postId + " not found"));
    }

    @Test
    void deleteAllImageByPostIdSuccessTest() throws Exception {
        Long postId = 1L;
        List<Long> deletedImageIds = List.of(101L, 102L, 103L);

        when(postResourceService.deleteAllPostImages(postId)).thenReturn(deletedImageIds);

        mockMvc.perform(delete(mainUrl + UrlUtils.ID + UrlUtils.IMAGE + UrlUtils.ALL, postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(deletedImageIds.size()))
                .andExpect(jsonPath("$[0]").value(101L))
                .andExpect(jsonPath("$[1]").value(102L))
                .andExpect(jsonPath("$[2]").value(103L));
    }

    @Test
    void deleteAllImageByPostIdWhenPostIdIsInvalidFailTest() throws Exception {
        Long postId = 0L;

        mockMvc.perform(delete(mainUrl + UrlUtils.ID + UrlUtils.IMAGE + UrlUtils.ALL, postId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteAllImageByPostIdWhenNoImagesExistFailTest() throws Exception {
        Long postId = 1L;

        when(postResourceService.deleteAllPostImages(postId))
                .thenThrow(new EntityNotFoundException("No images found for post with ID " + postId));

        mockMvc.perform(delete(mainUrl + UrlUtils.ID + UrlUtils.IMAGE + UrlUtils.ALL, postId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No images found for post with ID " + postId));
    }
}