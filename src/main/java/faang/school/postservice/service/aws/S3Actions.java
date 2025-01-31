package faang.school.postservice.service.aws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CopyObjectResponse;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.UploadPartCopyRequest;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Publisher;
import software.amazon.awssdk.services.s3.waiters.S3AsyncWaiter;

import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class S3Actions {

    private static final Logger logger = LoggerFactory.getLogger(S3Actions.class);
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


    public static S3AsyncClient getAsyncClient() {
        if (s3AsyncClient == null) {

            SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
                    .maxConcurrency(50)
                    .connectionTimeout(Duration.ofSeconds(60))
                    .readTimeout(Duration.ofSeconds(60))
                    .writeTimeout(Duration.ofSeconds(60))
                    .build();

            ClientOverrideConfiguration overrideConfig = ClientOverrideConfiguration.builder()
                    .apiCallTimeout(Duration.ofMinutes(2))
                    .apiCallAttemptTimeout(Duration.ofSeconds(90))
                    .retryStrategy(RetryMode.STANDARD)
                    .build();

            s3AsyncClient = S3AsyncClient.builder()
                    .region(Region.US_EAST_1)
                    .httpClient(httpClient)
                    .overrideConfiguration(overrideConfig)
                    .build();
        }
        return s3AsyncClient;
    }

    public CompletableFuture<Void> createBucketAsync(String bucketName) {
        CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                .bucket(bucketName)
                .build();

        CompletableFuture<CreateBucketResponse> response = getAsyncClient().createBucket(bucketRequest);
        return response.thenCompose(resp -> {
            S3AsyncWaiter s3Waiter = getAsyncClient().waiter();
            HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            CompletableFuture<WaiterResponse<HeadBucketResponse>> waiterResponseFuture =
                    s3Waiter.waitUntilBucketExists(bucketRequestWait);
            return waiterResponseFuture.thenAccept(waiterResponse -> {
                waiterResponse.matched().response().ifPresent(headBucketResponse -> {
                    logger.info(bucketName + " is ready");
                });
            });
        }).whenComplete((resp, ex) -> {
            if (ex != null) {
                throw new RuntimeException("Failed to create bucket", ex);
            }
        });
    }

    public CompletableFuture<List<String>> listAllObjectsAsync(String bucketName) {
        ListObjectsV2Request initialRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .maxKeys(1)
                .build();

        List<String> objectKeys = new ArrayList<>();
        ListObjectsV2Publisher paginator = getAsyncClient().listObjectsV2Paginator(initialRequest);
        return paginator.subscribe(response -> {
                    response.contents().forEach(s3Object -> {
                        objectKeys.add(s3Object.key());
                        logger.info("Object key: " + s3Object.key());
                    });
                }).thenApply(v -> objectKeys)
                .exceptionally(ex -> {
                    throw new RuntimeException("Failed to list objects", ex);
                });
    }

    public CompletableFuture<List<Bucket>> listBuckets() {
        return getAsyncClient().listBuckets()
                .thenApply(ListBucketsResponse::buckets)
                .exceptionally(throwable -> {
                    logger.error("Failed to list buckets", throwable);
                    throw new RuntimeException("Failed to list buckets", throwable);
                });
    }

    public CompletableFuture<Void> deleteBucketAsync(String bucket) {
        DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder()
                .bucket(bucket)
                .build();

        CompletableFuture<DeleteBucketResponse> response = getAsyncClient().deleteBucket(deleteBucketRequest);
        return response.thenAccept(deleteRes -> {
            logger.info(bucket + " was deleted.");
        }).exceptionally(ex -> {
            logger.error("Failed to delete bucket: " + bucket, ex);
            throw new RuntimeException("An S3 exception occurred during bucket deletion", ex);
        });
    }

    public CompletableFuture<PutObjectResponse> uploadLocalFileAsync(String bucketName, String key, String objectPath) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        CompletableFuture<PutObjectResponse> response = getAsyncClient().putObject(objectRequest, AsyncRequestBody.fromFile(Paths.get(objectPath)));
        return response.whenComplete((resp, ex) -> {
            if (ex != null) {
                throw new RuntimeException("Failed to upload file", ex);
            }
        });
    }

    public CompletableFuture<byte[]> getObjectBytesAsync(String bucketName, String keyName) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .key(keyName)
                .bucket(bucketName)
                .build();

        CompletableFuture<ResponseBytes<GetObjectResponse>> response = getAsyncClient().getObject(objectRequest, AsyncResponseTransformer.toBytes());
        return response.thenApply(ResponseBytes::asByteArray)
                .exceptionally(ex -> {
                    throw new RuntimeException("Failed to get object bytes from S3", ex);
                });
    }

    public CompletableFuture<Void> deleteObjectFromBucketAsync(String bucketName, String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        CompletableFuture<DeleteObjectResponse> response = getAsyncClient().deleteObject(deleteObjectRequest);
        return response.thenAccept(deleteRes -> {
            logger.info(key + " was deleted");
        }).exceptionally(ex -> {
            throw new RuntimeException("An S3 exception occurred during delete", ex);
        });
    }

    public CompletableFuture<String> copyBucketObjectAsync(String fromBucket, String objectKey, String toBucket) {
        CopyObjectRequest copyReq = CopyObjectRequest.builder()
                .copySource(fromBucket + "/" + objectKey)
                .destinationBucket(toBucket)
                .destinationKey(objectKey)
                .build();

        CompletableFuture<CopyObjectResponse> response = getAsyncClient().copyObject(copyReq);
        response.whenComplete((copyRes, ex) -> {
            if (copyRes != null) {
                logger.info("The " + objectKey + " was copied to " + toBucket);
            } else {
                throw new RuntimeException("An S3 exception occurred during copy", ex);
            }
        });

        return response.thenApply(CopyObjectResponse::copyObjectResult)
                .thenApply(Object::toString);
    }

    public CompletableFuture<Void> multipartUpload(String bucketName, String key) {
        int mB = 1024 * 1024;

        CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        return getAsyncClient().createMultipartUpload(createMultipartUploadRequest)
                .thenCompose(createResponse -> {
                    String uploadId = createResponse.uploadId();
                    System.out.println("Upload ID: " + uploadId);

                    // Upload part 1.
                    UploadPartRequest uploadPartRequest1 = UploadPartRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .uploadId(uploadId)
                            .partNumber(1)
                            .contentLength((long) (5 * mB)) // Specify the content length
                            .build();

                    CompletableFuture<CompletedPart> part1Future = getAsyncClient().uploadPart(uploadPartRequest1,
                                    AsyncRequestBody.fromByteBuffer(getRandomByteBuffer(5 * mB)))
                            .thenApply(uploadPartResponse -> CompletedPart.builder()
                                    .partNumber(1)
                                    .eTag(uploadPartResponse.eTag())
                                    .build());

                    // Upload part 2.
                    UploadPartRequest uploadPartRequest2 = UploadPartRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .uploadId(uploadId)
                            .partNumber(2)
                            .contentLength((long) (3 * mB))
                            .build();

                    CompletableFuture<CompletedPart> part2Future = getAsyncClient().uploadPart(uploadPartRequest2,
                                    AsyncRequestBody.fromByteBuffer(getRandomByteBuffer(3 * mB)))
                            .thenApply(uploadPartResponse -> CompletedPart.builder()
                                    .partNumber(2)
                                    .eTag(uploadPartResponse.eTag())
                                    .build());

                    // Combine the results of both parts.
                    return CompletableFuture.allOf(part1Future, part2Future)
                            .thenCompose(v -> {
                                CompletedPart part1 = part1Future.join();
                                CompletedPart part2 = part2Future.join();

                                CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
                                        .parts(part1, part2)
                                        .build();

                                CompleteMultipartUploadRequest completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder()
                                        .bucket(bucketName)
                                        .key(key)
                                        .uploadId(uploadId)
                                        .multipartUpload(completedMultipartUpload)
                                        .build();

                                // Complete the multipart upload
                                return getAsyncClient().completeMultipartUpload(completeMultipartUploadRequest);
                            });
                })
                .thenAccept(response -> System.out.println("Multipart upload completed successfully"))
                .exceptionally(ex -> {
                    System.err.println("Failed to complete multipart upload: " + ex.getMessage());
                    throw new RuntimeException(ex);
                });
    }

    public CompletableFuture<String> performMultiCopy(String toBucket, String bucketName, String key) {
        CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(toBucket)
                .key(key)
                .build();

        getAsyncClient().createMultipartUpload(createMultipartUploadRequest)
                .thenApply(createMultipartUploadResponse -> {
                    String uploadId = createMultipartUploadResponse.uploadId();
                    System.out.println("Upload ID: " + uploadId);

                    UploadPartCopyRequest uploadPartCopyRequest = UploadPartCopyRequest.builder()
                            .copySource(bucketName + "/" + key)
                            .bucket(toBucket)
                            .key(key)
                            .uploadId(uploadId)  // Use the valid uploadId.
                            .partNumber(1)  // Ensure the part number is correct.
                            .copySourceRange("bytes=0-1023")  // Adjust range as needed
                            .build();

                    return getAsyncClient().uploadPartCopy(uploadPartCopyRequest);
                })
                .thenCompose(uploadPartCopyFuture -> uploadPartCopyFuture)
                .whenComplete((uploadPartCopyResponse, exception) -> {
                    if (exception != null) {
                        // Handle any exceptions.
                        logger.error("Error during upload part copy: " + exception.getMessage());
                    } else {
                        // Successfully completed the upload part copy.
                        System.out.println("Upload Part Copy completed successfully. ETag: " + uploadPartCopyResponse.copyPartResult().eTag());
                    }
                });
        return null;
    }

    private static ByteBuffer getRandomByteBuffer(int size) {
        ByteBuffer buffer = ByteBuffer.allocate(size);
        for (int i = 0; i < size; i++) {
            buffer.put((byte) (Math.random() * 256));
        }
        buffer.flip();
        return buffer;
    }
}
