package faang.school.postservice.service;

import faang.school.postservice.config.AwsS3ApiConfig;
import faang.school.postservice.service.aws.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class FileService {
    private final S3Service s3Service;
    private final AwsS3ApiConfig awsS3ApiConfig;

    public List<String> uploadFiles(List<MultipartFile> files) {

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

                String key = UUID.randomUUID() + "-" + file.getOriginalFilename();
                byte[] fileBytes = image != null ? bufferedImageToByteArray(image, file.getContentType()) : file.getBytes();
                s3Service.addFile(awsS3ApiConfig.getBucket(), key, fileBytes).join();
            } catch (IOException e) {
                throw new RuntimeException("Failed to process file", e);
            }
        }
        return List.of(); // Return the list of file keys
    }

    public void deleteFiles(List<String> fileIds) {
        for (String fileId : fileIds) {
            s3Service.removeFile(awsS3ApiConfig.getBucket(), fileId).join();
        }
    }

    public List<String> getFileKeysByPostId(Long postId) {
        // Implement the logic to retrieve file keys by postId
        // This is a placeholder implementation
        return List.of("fileKey1", "fileKey2", "fileKey3");
    }


    private BufferedImage resizeImageIfNeeded(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        if (width > 1080 || height > 1080) {
            int newWidth = width > 1080 ? 1080 : width;
            int newHeight = height > 1080 ? 1080 : height;
            Image tmp = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = resized.createGraphics();
            g2d.drawImage(tmp, 0, 0, null);
            g2d.dispose();
            return resized;
        }
        return image;
    }

    private byte[] bufferedImageToByteArray(BufferedImage image, String contentType) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String formatName = contentType.split("/")[1];
        ImageIO.write(image, formatName, baos);
        return baos.toByteArray();
    }
}
