package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

public class PostMapperTest {

    private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @Test
    void toDto_ShouldMapPostToPostDto() {
        Post post = new Post();
        post.setId(1L);
        post.setAuthorId(10L);
        post.setProjectId(20L);
        post.setContent("Test Content");

        PostDto postDto = postMapper.toDto(post);

        assertNotNull(postDto);
        assertEquals(10L, postDto.userId());
        assertEquals(20L, postDto.projectId());
        assertEquals("Test Content", postDto.content());
    }

    @Test
    void toEntity_ShouldMapPostDtoToPost() {
        PostDto postDto = new PostDto(1L, "Test Content", 10L, 20L, null, 1);
        Post post = postMapper.toEntity(postDto);

        assertNotNull(post);
        assertEquals(10L, post.getAuthorId());
        assertEquals(20L, post.getProjectId());
        assertEquals("Test Content", post.getContent());
        assertNull(post.getId());
        assertFalse(post.isPublished());
    }
}