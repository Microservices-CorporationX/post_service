package faang.school.postservice.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;

@Component
@Slf4j
public class ResourceValidator {

    private static final int MAX_IMAGE_WIDTH_PX = 1024;
    private static final int MAX_IMAGE_HEIGHT_PX = 566;
    private static final int MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024;

    public void validateResourceCompression(int width, int height) {
        if (width > height) {
            log.debug("Image width: {}, height: {}, image is horizontal", width, height);
        }
    }
}
