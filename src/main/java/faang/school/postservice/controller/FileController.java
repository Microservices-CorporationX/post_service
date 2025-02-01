package faang.school.postservice.controller;

import faang.school.postservice.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/post/files")
@RestController
public class FileController {
    private final FileService fileService;

    @PostMapping
    public void addFiles(@RequestParam List<MultipartFile> files, @RequestParam("postId") Long postId) {
        List<String> fileKeys = fileService.uploadFiles(postId, files);
        //postService.addFilesToPost(postId, fileKeys);
        //fileKey
    }

    @DeleteMapping
    public void deleteFiles(@RequestBody List<String> fileIds) {
        fileService.deleteFiles(fileIds);
    }

    @GetMapping("/presigned-url")
    public ResponseEntity<String> getPresignedUrl(@RequestParam("fileId") String fileId) {
        String presignedUrl = fileService.getPresignedUrl(fileId);
        return ResponseEntity.ok(presignedUrl);
    }
}
