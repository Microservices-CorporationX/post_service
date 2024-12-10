package faang.school.postservice.controller;

import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.service.ResourceService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping("/posts/{postId}")
    public List<ResourceDto> uploadFiles(@PathVariable @NotNull @Positive Long postId,
                                         @RequestBody List<MultipartFile> files) {
        return resourceService.uploadFiles(postId, files);
    }

    @PutMapping("/{resourceId}")
    public ResourceDto updateFiles(@PathVariable @NotNull @Positive Long resourceId,
                                   @RequestBody MultipartFile file) {
        return resourceService.updateFiles(resourceId, file);
    }

    @DeleteMapping("/{resourceId}")
    public void deleteFiles(@PathVariable @NotNull @Positive Long resourceId) {
        resourceService.deleteFiles(resourceId);
    }
}
