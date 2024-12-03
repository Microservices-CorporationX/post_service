package faang.school.postservice.service.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.postservice.exception.FileException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@RequiredArgsConstructor
@Service
@Slf4j
public class S3Service {
    private final AmazonS3 amazonS3;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    public void updateResource(MultipartFile multipartFile, String key) {
        completeRemoval(key);
        uploadResource(multipartFile, key);
    }

    public void completeRemoval(String key) {
        log.info("calling amazonS3 deleteObject method with bucketName and key");
        amazonS3.deleteObject(bucketName, key);
    }

    public String generatePresignedUrl(String key) {
        log.info("generate URL request");
        GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(bucketName, key)
                .withMethod(HttpMethod.GET)
                .withExpiration(Date.from(Instant.now().plus(Duration.ofHours(12))));

        log.info("calling amazonS3 generatePresignedUrl method with generated URL");
        URL url = amazonS3.generatePresignedUrl(urlRequest);

        return url.toString();
    }

    public InputStream downloadResource(String key) {
        log.info("download resource request");
        try {
            S3Object object = amazonS3.getObject(bucketName, key);
            return object.getObjectContent();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new FileException(e.getMessage(), e);
        }
    }

    public void uploadResource(MultipartFile multipartFile, String key) {
        log.info("generating objectMetaData");
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());
        try {
            log.info("create putObjectRequest");

            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, multipartFile.getInputStream(), objectMetadata
            );

            log.info("calling amazonS3 putObject method with generated request");
            amazonS3.putObject(putObjectRequest);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new FileException(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}