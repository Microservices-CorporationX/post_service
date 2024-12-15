package faang.school.postservice.service;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.dto.resource.ResourceInfoDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;

    public List<ResourceDto> uploadResources(Post post, List<ResourceInfoDto> resourcesDto) {
        if (post.getId() == null) {
            throw new DataValidationException("Post id is required");
        }

        List<Resource> resources = resourcesDto.stream()
                .map(r -> upload(post, r))
                .toList();
        resources = resourceRepository.saveAll(resources);
        return resources.stream().map(resourceMapper::toDto).toList();
    }

    private Resource upload(Post post, ResourceInfoDto resourceDto) {
        s3Service.uploadFile(resourceDto);
        return Resource.builder()
                .name(resourceDto.getName())
                .key(resourceDto.getKey())
                .size(resourceDto.getBytes().length)
                .type(resourceDto.getType())
                .createdAt(LocalDateTime.now())
                .post(post)
                .build();
    }

}
