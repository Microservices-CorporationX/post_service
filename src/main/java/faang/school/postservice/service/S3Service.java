package faang.school.postservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.postservice.util.ImageUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 s3Client;
    private final ImageUtil imageUtil;

    @Value("${s3.bucketName}")
    private String bucketName;

    public String uploadImageFile(MultipartFile file, String folder, BufferedImage image) {
        String type = file.getContentType();
        String originalName = file.getOriginalFilename();
        String key = String.format("%s/%d:%s", folder, System.currentTimeMillis(), originalName);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(type);

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, imageUtil.bufferedImageToInputStream(image, file), objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (Exception e) {
            log.warn("Unable to upload image file to S3: {}", e.getMessage());
            throw new IllegalStateException("Unable to upload file to S3", e);
        }

        return key;
    }

    public void deleteResource(@NotBlank String key) {
        s3Client.deleteObject(bucketName, key);
    }
}
