package faang.school.postservice.service.resource;

import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ResourceServiceTests {
    @Mock
    private PostRepository postRepository;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private S3Service s3Service;
    @Spy
    private final ResourceMapper resourceMapper = Mappers.getMapper(ResourceMapper.class);

    @InjectMocks
    private ResourceService resourceService;

    private Post post;
    private Resource resource;
    private ResourceDto resourceDto;
    private String key = "key";

    @BeforeEach
    void setUp() {
        resourceDto = new ResourceDto();
        resourceDto.setId(1L);
        resourceDto.setKey(key);

    }

    @Test
    void testAddFilesToPostIllegalStateException() {
        List<MultipartFile> files = prepareMultipartFiles(10);

        assertThrows(IllegalStateException.class,
                () -> resourceService.addFilesToPost(1L, files));
    }

    @Test
    void testAddFilesToPostIllegalArgumentException() {
        assertThrows(IllegalStateException.class,
                () -> resourceService.addFilesToPost(1L, new ArrayList<>()));
    }

    @Test
    void testAddFilesToPost() {
        preparePost();
        prepareResource();
        List<MultipartFile> files = prepareMultipartFiles(9);
        when(postRepository.findById(anyLong())).thenReturn(Optional.ofNullable(post));

        List<ResourceDto> result = resourceService.addFilesToPost(1L, files);

        verify(postRepository, times(1)).findById(anyLong());
        assertEquals(result.size(), files.size());
    }

    @Test
    void testaddFileToPostIllegalArgumentException() {
        MultipartFile file = prepareMultipartFiles(1).get(0);
        assertThrows(IllegalArgumentException.class,
                () -> resourceService.addFileToPost(1L, file));
    }

    @Test
    void testAddFileToPost() {
        preparePost();
        prepareResource();
        MultipartFile file = prepareMultipartFiles(1).get(0);
        when(postRepository.findById(anyLong())).thenReturn(Optional.ofNullable(post));
        when(resourceRepository.findByPostId(anyLong())).thenReturn(List.of(resource));
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);
        when(resourceMapper.toDto(any(Resource.class))).thenReturn(resourceDto);

        ResourceDto result = resourceService.addFileToPost(1L, file);

        verify(postRepository, times(1)).findById(anyLong());
        verify(resourceRepository, times(1)).findByPostId(anyLong());
        assertEquals(result, resourceDto);
    }

    @Test
    void testRemoveFileFromPost() {
        resourceService.removeFileFromPost(resourceDto);

        verify(resourceRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void testGetFilesForPost() {
        preparePost();
        prepareResource();
        when(resourceRepository.findByPostId(anyLong())).thenReturn(List.of(resource));

        List<InputStream> inputStreams = resourceService.getFilesForPost(1L);

        verify(resourceRepository, times(1)).findByPostId(anyLong());
    }


    private List<MultipartFile> prepareMultipartFiles(int count) {
        List<MultipartFile> files = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            MultipartFile multipartFile = new MockMultipartFile(
                    "file" + i,
                    "test.png",
                    "image/png",
                    "test".getBytes()
            );
            files.add(multipartFile);
        }
        return files;
    }

    private void preparePost() {
        post = new Post();
        post.setId(1L);
    }

    private void prepareResource() {
        resource = new Resource();
        resource.setId(1L);
    }

}
