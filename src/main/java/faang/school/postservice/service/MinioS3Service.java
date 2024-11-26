package faang.school.postservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.postservice.exception.FileException;
import faang.school.postservice.model.Resource;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioS3Service {

    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Transactional
    public Resource uploadFile(MultipartFile multipartFile, String folder) {
        ObjectMetadata objectMetaData = new ObjectMetadata();
        objectMetaData.setContentType(multipartFile.getContentType());
        objectMetaData.setContentLength(multipartFile.getSize());
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), multipartFile.getOriginalFilename());
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, multipartFile.getInputStream(), objectMetaData);
            s3Client.putObject(putObjectRequest);
        } catch (IOException e) {
            log.error("Error uploading file: {}", e.getMessage(), e);
            throw new FileException("Error uploading file: " + e.getMessage());
        }
        Resource resource = new Resource();
        resource.setKey(key);
        resource.setSize(multipartFile.getSize());
        resource.setCreatedAt(LocalDateTime.now());
        resource.setName(multipartFile.getOriginalFilename());
        resource.setType(multipartFile.getContentType());
        return resource;
    }
}
