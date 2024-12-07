package faang.school.postservice.controller.resource;

import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.service.resource.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resource")
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping("{postId}/addFiles")
    public List<ResourceDto> addFilesToPost(@PathVariable long postId, @RequestBody List<MultipartFile> files) {
        return resourceService.addFilesToPost(postId, files);
    }

    @PostMapping("{postId}/addFile")
    public ResourceDto addFileToPost(@PathVariable long postId, @RequestBody MultipartFile file) {
        return resourceService.addFileToPost(postId, file);
    }

    @DeleteMapping("/removeFile")
    public void removeFileFromPost(@RequestBody @Valid ResourceDto resourceDto) {
        resourceService.removeFileFromPost(resourceDto);
    }

    @GetMapping("{postId}/getFiles")
    public List<ResponseEntity<byte[]>> getFilesForPost(@PathVariable long postId) {

        List<InputStream> inputStreams = resourceService.getFilesForPost(postId);

        return inputStreams.stream()
                .map(input -> {
                    try {
                        byte[] imageBytes = input.readAllBytes();
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.IMAGE_PNG);
                        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }
}
