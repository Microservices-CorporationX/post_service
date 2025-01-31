package faang.school.postservice.service.aws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.async.AsyncRequestBody;

import software.amazon.awssdk.services.s3.S3AsyncClient;

import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.util.concurrent.CompletableFuture;

public class S3Service {

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);
    private static S3AsyncClient s3AsyncClient;


    public CompletableFuture<Void> uploadFile(String bucketName, String key, byte[] fileBytes) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        return s3AsyncClient.putObject(putObjectRequest, AsyncRequestBody.fromBytes(fileBytes))
                .thenAccept(response -> logger.info("File uploaded successfully: {}", key))
                .exceptionally(ex -> {
                    logger.error("Failed to upload file: {}", key, ex);
                    throw new RuntimeException("Failed to upload file", ex);
                });
    }

    public CompletableFuture<Void> deleteFile(String bucketName, String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        return s3AsyncClient.deleteObject(deleteObjectRequest)
                .thenAccept(response -> logger.info("File deleted successfully: {}", key))
                .exceptionally(ex -> {
                    logger.error("Failed to delete file: {}", key, ex);
                    throw new RuntimeException("Failed to delete file", ex);
                });
    }

}
