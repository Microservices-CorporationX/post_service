package faang.school.postservice.service;

import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public Resource saveResource(Resource resource) {
        Resource result = resourceRepository.save(resource);
        log.info("Resource saved: {}", result.getId());
        return result;
    }

    public void deleteResource(Long resourceId) {
        resourceRepository.deleteById(resourceId);
        log.info("Resource deleted: {}", resourceId);
    }

    public Long findIdByKey(String key) {
        return resourceRepository.findIdByKey(key).orElseThrow(() -> new EntityNotFoundException("Resource with key '" + key + "' not found"));
    }
}
