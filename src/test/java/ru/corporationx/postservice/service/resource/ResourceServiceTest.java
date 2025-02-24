package ru.corporationx.postservice.service.resource;

import ru.corporationx.postservice.dto.resource.ResourceDto;
import ru.corporationx.postservice.dto.resource.ResourceInfoDto;
import ru.corporationx.postservice.exception.DataValidationException;
import ru.corporationx.postservice.mapper.ResourceMapper;
import ru.corporationx.postservice.model.Post;
import ru.corporationx.postservice.model.Resource;
import ru.corporationx.postservice.repository.ResourceRepository;
import ru.corporationx.postservice.service.resource.ResourceService;
import ru.corporationx.postservice.service.s3.S3Service;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {
    @Mock
    S3Service s3Service;
    @Mock
    ResourceRepository resourceRepository;
    @Mock
    ResourceMapper resourceMapper;
    @InjectMocks
    ResourceService resourceService;

    @Test
    void testUploadResourceNoPostId() {
        Post post = Post.builder().build();
        Assertions.assertThrows(DataValidationException.class, () -> resourceService.uploadResources(post, List.of()));
    }

    @Test
    void testUploadResourceOk() {
        Mockito.doNothing().when(s3Service).uploadFile(any());
        Mockito.when(resourceRepository.saveAll(any())).thenReturn(List.of(new Resource(), new Resource()));
        Mockito.when(resourceMapper.toDto(any())).thenReturn(ResourceDto.builder().build());

        Post post = Post.builder()
                .id(1L)
                .build();

        List<ResourceInfoDto> inputFiles = List.of(
                ResourceInfoDto.builder()
                        .name("name1")
                        .key("key1")
                        .bytes(new byte[1])
                        .build(),
                ResourceInfoDto.builder()
                        .name("name2")
                        .key("key2")
                        .bytes(new byte[1])
                        .build()
        );

        List<ResourceDto> result = resourceService.uploadResources(post, inputFiles);

        assertEquals(2, result.size());
        Mockito.verify(s3Service, times(2)).uploadFile(any());
        Mockito.verify(resourceRepository, times(1)).saveAll(any());
        Mockito.verify(resourceMapper, times(2)).toDto(any());
    }

}
