package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.post.Hashtag;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.repository.post.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Spy
    private PostMapper postMapper;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    public void testGetPostsByHashtag() {
        UUID uuid = UUID.randomUUID();
        when(postRepository.findByHashtag(eq("hashtag"))).thenReturn(List.of(getPost(uuid)));

        List<PostResponseDto> actualResult = postService.getPostsByHashtag("hashtag");

        assertEquals(getExpectedResult(uuid), actualResult);
    }

    private List<PostResponseDto> getExpectedResult(UUID uuid) {
        return Stream.of(getPost(uuid))
                .map(postMapper::toDto)
                .toList();
    }

    private Post getPost(UUID uuid) {
        return Post.builder()
                .id(1L)
                .hashtags(List.of(getHashtag(uuid)))
                .build();
    }

    private Hashtag getHashtag(UUID uuid) {
        return Hashtag
                .builder()
                .id(uuid)
                .name("hashtag")
                .build();
    }
}