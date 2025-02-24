package ru.corporationx.postservice.service.resource;

import ru.corporationx.postservice.dto.resource.ResourceDto;
import ru.corporationx.postservice.dto.resource.ResourceInfoDto;
import ru.corporationx.postservice.exception.DataValidationException;
import ru.corporationx.postservice.mapper.ResourceMapper;
import ru.corporationx.postservice.model.Post;
import ru.corporationx.postservice.model.Resource;
import ru.corporationx.postservice.repository.ResourceRepository;
import ru.corporationx.postservice.service.s3.S3Service;
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
