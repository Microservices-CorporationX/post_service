package faang.school.postservice.service;

import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.exception.ResourceNotFoundException;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.ResourceType;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.validator.FileValidator;
import faang.school.postservice.validator.RequestValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {

    @InjectMocks
    private ResourceService resourceService;

    @Mock
    private PostService postService;

    @Mock
    private S3Service s3Service;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private RequestValidator requestValidator;

    @Mock
    private FileValidator fileValidator;

    @Mock
    private ResourceMapper resourceMapper;

    private Post post;
    private Resource resource;
    private MultipartFile file;
    private BufferedImage image;

    @BeforeEach
    public void setUp() {
        post = Post.builder().id(1L).build();

        resource = Resource.builder()
                .id(1L)
                .post(post)
                .key("test-key")
                .type(ResourceType.IMAGE)
                .build();

        file = mock(MultipartFile.class);
        image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
    }

    @Test
    public void testUploadFiles() {
        when(postService.findPostById(anyLong())).thenReturn(Optional.of(post));
        when(file.getContentType()).thenReturn("image/png");
        when(fileValidator.getValidatedImage(any(MultipartFile.class))).thenReturn(image);
        when(s3Service.uploadImageFile(any(MultipartFile.class), anyString(), any(BufferedImage.class))).thenReturn("test-key");
        when(resourceMapper.toResourceDto(anyList())).thenReturn(List.of(ResourceDto.builder()
                .id(1L)
                .postId(1L)
                .type(ResourceType.IMAGE)
                .build()));

        List<ResourceDto> resourceDtos = resourceService.uploadFiles(1L, List.of(file));

        verify(postService, times(1)).findPostById(anyLong());
        verify(fileValidator, times(1)).validateNumberOfFiles(anyList(), anyLong());
        verify(fileValidator, times(1)).getValidatedImage(any(MultipartFile.class));
        verify(s3Service, times(1)).uploadImageFile(any(MultipartFile.class), anyString(), any(BufferedImage.class));
        verify(resourceRepository, times(1)).saveAll(anyList());
        assertNotNull(resourceDtos);
    }

    @Test
    public void testUpdateFiles() {
        when(resourceRepository.findById(anyLong())).thenReturn(Optional.of(resource));
        when(file.getContentType()).thenReturn("image/png");
        when(fileValidator.getValidatedImage(any(MultipartFile.class))).thenReturn(image);
        when(s3Service.uploadImageFile(any(MultipartFile.class), anyString(), any(BufferedImage.class))).thenReturn("new-key");
        when(resourceMapper.toResourceDto(any(Resource.class))).thenReturn(ResourceDto.builder()
                .id(1L)
                .postId(1L)
                .type(ResourceType.IMAGE)
                .build());

        ResourceDto resourceDto = resourceService.updateFiles(1L, file);

        verify(resourceRepository, times(1)).findById(anyLong());
        verify(fileValidator, times(1)).getValidatedImage(any(MultipartFile.class));
        verify(s3Service, times(1)).deleteResource(anyString());
        verify(s3Service, times(1)).uploadImageFile(any(MultipartFile.class), anyString(), any(BufferedImage.class));
        verify(resourceRepository, times(1)).save(any(Resource.class));
        assertNotNull(resourceDto);
    }

    @Test
    public void testDeleteFiles() {
        when(resourceRepository.findById(anyLong())).thenReturn(Optional.of(resource));

        resourceService.deleteFiles(1L);

        verify(resourceRepository, times(1)).findById(anyLong());
        verify(s3Service, times(1)).deleteResource(anyString());
        verify(resourceRepository, times(1)).delete(any(Resource.class));
    }

    @Test
    public void testFindResourceById() {
        when(resourceRepository.findById(anyLong())).thenReturn(Optional.of(resource));

        Resource foundResource = resourceService.findResourceById(1L);

        verify(resourceRepository, times(1)).findById(anyLong());
        assertNotNull(foundResource);
    }

    @Test
    public void testFindResourceByIdNotFound() {
        when(resourceRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> resourceService.findResourceById(1L));
    }
}