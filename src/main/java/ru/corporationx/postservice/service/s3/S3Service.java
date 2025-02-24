package ru.corporationx.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import ru.corporationx.postservice.dto.resource.ResourceInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Slf4j
@Service
public class S3Service {
    private final String bucketName;
    private final AmazonS3 s3Client;

    public S3Service(@Value("${services.s3.postBucketName}") String bucketName, AmazonS3 s3Client) {
        this.bucketName = bucketName;
        this.s3Client = s3Client;
    }

    public void uploadFile(ResourceInfoDto file) {
        try {
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentType(file.getType());
            PutObjectRequest putObject = new PutObjectRequest(bucketName, file.getKey(),
                    new ByteArrayInputStream(file.getBytes()), meta);
            s3Client.putObject(putObject);
        } catch (Exception e) {
            throw new IllegalStateException("Failed uploading file", e);
        }
    }

}
