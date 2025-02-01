package faang.school.postservice.service;

import faang.school.postservice.config.AwsS3ApiConfig;
import faang.school.postservice.service.aws.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletionException;


@RequiredArgsConstructor
@Service
public class FileService {
    private final S3Service s3Service;
    private final AwsS3ApiConfig awsS3ApiConfig;

    public List<String> uploadFiles(Long postId, List<MultipartFile> files) {

        if (files.size() > 10) {
            throw new IllegalArgumentException("Cannot upload more than 10 files per post");
        }

        List<String> fileKeys = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("File size must be less than or equal to 5MB");
            }

            String contentType = file.getContentType();
            if (contentType == null || (!contentType.startsWith("video/") && !contentType.startsWith("audio/") && !contentType.startsWith("image/"))) {
                throw new IllegalArgumentException("Unsupported file type");
            }

            try {
                BufferedImage image = null;

                if (contentType.startsWith("image/")) {
                    image = ImageIO.read(file.getInputStream());
                    if (image != null) {
                        image = resizeImageIfNeeded(image);
                    }
                }

                byte[] fileBytes = image != null ? bufferedImageToByteArray(image, file.getContentType()) : file.getBytes();
                Map<String, String> metadata = new HashMap<>();
                metadata.put("Content-Type", contentType);
                metadata.put("Content-Length", String.valueOf(fileBytes.length));
                metadata.put("Original-Filename", file.getOriginalFilename());
                String key = "post/" + postId + "/" + UUID.randomUUID();
                PutObjectResponse result = s3Service.uploadFileAsync(awsS3ApiConfig.getBucket(), key, metadata, fileBytes).join();
                fileKeys.add(result.eTag());
            } catch (IOException e) {
                throw new RuntimeException("Failed to process file", e);
            }
        }
        return List.of(); // Return the list of file keys
    }

    public void deleteFiles(List<String> fileIds) {
        for (String fileId : fileIds) {
            s3Service.deleteFileAsync(awsS3ApiConfig.getBucket(), fileId).join();
        }
    }

    public String getPresignedUrl(String fileId) {
        return s3Service.createPresignedGetUrl(awsS3ApiConfig.getBucket(), fileId);
    }

    private BufferedImage resizeImageIfNeeded(BufferedImage image) {
        //assuming image size cannot be different
        int width = image.getWidth();
        int height = image.getHeight();
        int newWidth = width;
        int newHeight = height;

        if (width == height) {
            newWidth = Math.min(newWidth, 1080);
            newHeight = newWidth;

        } else {
            float aspectRatio = (float) width / height;
            newWidth = Math.min(newWidth, 1080);
            newHeight = Math.round(1080 / aspectRatio);
            if (newHeight > 566) {
                newHeight =  Math.min(newWidth, 566);
                newWidth = Math.round(newHeight * aspectRatio);
            }
        }

        if (width != newWidth || height != newHeight) {
            Image tmp = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            BufferedImage resized = new BufferedImage(newWidth, newHeight,  BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = resized.createGraphics();
            //g2d.drawImage(tmp, 0, 0, null);
            AffineTransform at = AffineTransform.getScaleInstance((double) newWidth / width, (double) newHeight / height);
            g2d.drawRenderedImage(image, at);
            g2d.dispose();
            return resized;
        }

        return image;
    }

    private byte[] bufferedImageToByteArray(BufferedImage image, String contentType) throws IOException {
        if (image == null) {
            throw new IllegalArgumentException("BufferedImage is null");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String formatName = contentType != null && contentType.contains("/") ? contentType.split("/")[1] : "png";

        boolean result = ImageIO.write(image, formatName, baos);
        if (!result) {
            throw new IOException("Failed to write image to ByteArrayOutputStream");
        }

        return baos.toByteArray();
    }
}
