package faang.school.postservice.service.image;

import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Slf4j
@Service
public class ImageResizeService {
    private static final String IMAGE_FORMAT = "JPEG";
    private static final String CONVERTING_IMAGE_ERROR_MESSAGE = "Resizing image write error";
    private static final String RESIZING_IMAGE_ERROR_MESSAGE = "Image resizing error";

    public byte[] resizeAndConvert(MultipartFile file, int maxWidth, int maxHeight) {
        try {
            return resizeAndConvert(ImageIO.read(file.getInputStream()), maxWidth, maxHeight);
        } catch (Exception e) {
            log.error(RESIZING_IMAGE_ERROR_MESSAGE, e);
            throw new IllegalStateException(RESIZING_IMAGE_ERROR_MESSAGE, e);
        }
    }

    private byte[] resizeAndConvert(BufferedImage image, int maxWidth, int maxHeight) {
        image = resize(image, maxWidth, maxHeight);
        return convert(image);
    }

    private BufferedImage resize(BufferedImage image, int maxWidth, int maxHeight) {
        int width = image.getWidth();
        int height = image.getHeight();

        if (width == height) { // square
            int newSize = Math.min(maxHeight, maxWidth);
            image = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, newSize, newSize);
        } else {
            int newWidth = Math.min(maxWidth, width);
            int newHeight = Math.min(maxHeight, height);
            image = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, newWidth, newHeight);
        }
        return image;
    }

    private byte[] convert(BufferedImage image) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, IMAGE_FORMAT, os);
            return os.toByteArray();
        } catch (Exception e) {
            log.error(CONVERTING_IMAGE_ERROR_MESSAGE, e);
            throw new IllegalStateException(CONVERTING_IMAGE_ERROR_MESSAGE, e);
        }
    }

}
