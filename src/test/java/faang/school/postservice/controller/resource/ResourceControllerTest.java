package faang.school.postservice.controller.resource;

import faang.school.postservice.service.resource.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ResourceControllerTest {
    @InjectMocks
    private ResourceController resourceController;
    @Mock
    private ResourceService resourceService;
    @Mock
    private MultipartFile file;
    long postId = 1L;
    long imageId = 5L;

    @Test
    void addResourceSuccessful() {
        resourceController.addResource(postId, file);
        Mockito.verify(resourceService).addResource(postId, file);
    }

    @Test
    void deleteResourceSuccessful() {
        resourceController.deleteResource(postId, imageId);
        Mockito.verify(resourceService).deleteResource(postId, imageId);
    }
}