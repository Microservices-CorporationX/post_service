package faang.school.postservice.service;

import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {

    private final ResourceRepository resourceRepository;

    @Transactional
    public Resource saveResource(Resource resource) {
        Resource result = resourceRepository.save(resource);
        log.info("Resource saved: {}", result.getId());
        return result;
    }
}
