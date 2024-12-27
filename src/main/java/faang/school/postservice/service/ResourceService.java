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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {

    private final static int MAX_FILES = 10;

    private final PostService postService;
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final RequestValidator requestValidator;
    private final FileValidator fileValidator;
    private final ResourceMapper resourceMapper;

    public List<ResourceDto> uploadFiles(Long postId, List<MultipartFile> files) {
        Post post = postService.findPostById(postId);

        requestValidator.validateAuthorUpdatesPost(post);
        fileValidator.validateNumberOfFiles(files, MAX_FILES);
        List<BufferedImage> images = files.stream()
                .map(fileValidator::getValidatedImage)
                .toList();

        String folder = "post_" + postId;
        List<String> keys = uploadFilesToCloud(files, folder, images);
        List<Resource> resources = buildResources(keys, files);

        resources.forEach(resource -> resource.setPost(post));

        resourceRepository.saveAll(resources);
        return resourceMapper.toResourceDto(resources);
    }

    public ResourceDto updateFiles(Long resourceId, MultipartFile file) {
        Resource resource = findResourceById(resourceId)
                .orElseThrow(() -> {
                    log.warn("Resource with ID {} not found", resourceId);
                    return new ResourceNotFoundException("Resource", "id", resourceId);
                });
        s3Service.deleteResource(resource.getKey());

        List<ResourceDto> resourceDtos = uploadFiles(resource.getPostId(), List.of(file));
        ResourceDto updatedResourceDto = resourceDtos.get(0);

        Resource updatedResource = resourceMapper.toEntity(updatedResourceDto);
        updatedResource.setId(resourceId);

        resourceRepository.save(updatedResource);

        return updatedResourceDto;
    }

    public void deleteFiles(Long resourceId) {
        Resource resource = findResourceById(resourceId)
                .orElseThrow(() -> {
                    log.warn("Resource with ID {} not found", resourceId);
                    return new ResourceNotFoundException("Resource", "id", resourceId);
                });
        requestValidator.validateAuthorUpdatesPost(resource.getPost());
        s3Service.deleteResource(resource.getKey());
        resourceRepository.delete(resource);
    }

    public Optional<Resource> findResourceById(Long resourceId) {
        return resourceRepository.findById(resourceId);
    }

    private List<String> uploadFilesToCloud(List<MultipartFile> files, String folder, List<BufferedImage> images) {
        return files.stream()
                .map(file -> s3Service.uploadImageFile(file, folder, images.get(files.indexOf(file))))
                .toList();
    }

    private Resource buildResource(String key, MultipartFile file) {
        String name = file.getOriginalFilename();
        BigInteger size = BigInteger.valueOf(file.getSize());
        ResourceType type = ResourceType.getResourceType(file.getContentType());
        return Resource.builder()
                .key(key)
                .name(name)
                .size(size)
                .type(type)
                .build();
    }

    private List<Resource> buildResources(List<String> keys, List<MultipartFile> files) {
        return files.stream()
                .map(file -> buildResource(
                        keys.get(files.indexOf(file)),
                        file))
                .toList();
    }
}


