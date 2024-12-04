package faang.school.postservice.validator;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.ImageProcessingException;
import faang.school.postservice.model.ResourceType;
import faang.school.postservice.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class FileValidator {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final int MAX_WIDTH = 1080;
    private static final int MAX_HEIGHT = 1080;
    private static final int MIN_WIDTH = 200;
    private static final int MIN_HEIGHT = 200;

    private final ImageUtil imageUtil;

    public BufferedImage getValidatedImage(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            log.warn("File size exceeds the maximum limit of 5 MB");
            throw new DataValidationException("File size exceeds the maximum limit of 5 MB");
        }

        ResourceType resourceType = ResourceType.getResourceType(file.getContentType());
        if (resourceType != ResourceType.IMAGE) {
            log.warn("Invalid file type, only images are allowed");
            throw new DataValidationException("Invalid file type, only images are allowed");
        }

        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image.getWidth() > MAX_WIDTH || image.getHeight() > MAX_HEIGHT) {
                log.warn("Image resolution exceeds the maximum limit, resizing the image");
                return imageUtil.resizeImage(image, MAX_WIDTH, MAX_HEIGHT);
            } else if (image.getWidth() < MIN_WIDTH || image.getHeight() < MIN_HEIGHT) {
                log.warn("Image resolution is below the minimum limit, resizing the image");
                return imageUtil.resizeImage(image, MIN_WIDTH, MIN_HEIGHT);
            } else {
                return image;
            }
        } catch (IOException e) {
            log.error("Error reading image file", e);
            throw new ImageProcessingException("Error reading image file", e);
        }
    }

    public void validateNumberOfFiles(List<MultipartFile> files, long numberOfFiles) {
        if (files.size() > numberOfFiles) {
            log.warn("Cannot upload more than {} files", numberOfFiles);
            throw new DataValidationException(
                    String.format("Cannot upload more than %d files", numberOfFiles));
        }
    }
}
