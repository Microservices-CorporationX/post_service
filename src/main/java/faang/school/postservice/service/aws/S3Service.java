package faang.school.postservice.service.aws;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Actions s3Actions;

    public CompletableFuture<Boolean> checkAndRecreateBucket(String bucketName) {
        return s3Actions.listBuckets()
                .thenApply(buckets -> buckets.stream().anyMatch(bucket -> bucket.name().equals(bucketName)))
                .thenCompose(exists -> {
                    if (!exists) {
                        return s3Actions.createBucketAsync(bucketName).thenApply(bucket -> true);
                    }
                    return CompletableFuture.completedFuture(true);
                });
    }

    public CompletableFuture<Void> addFile(String bucketName, String key, byte[] fileBytes) {
        return s3Actions.uploadFile(bucketName, key, fileBytes);
    }

    public CompletableFuture<Void> removeFile(String bucketName, String key) {
        return s3Actions.deleteFile(bucketName, key);
    }

    public CompletableFuture<List<String>> listFiles(String bucketName) {
        List<String> objectKeys = new ArrayList<>();
        return s3Actions.listAllObjectsAsync(bucketName).thenApply(v -> objectKeys);
    }

    public CompletableFuture<byte[]> getFileById(String bucketName, String key, String path) {
        return s3Actions.getObjectBytesAsync(bucketName, key);
    }
}
