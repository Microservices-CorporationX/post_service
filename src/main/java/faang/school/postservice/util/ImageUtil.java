package faang.school.postservice.util;

import faang.school.postservice.exception.ImageProcessingException;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class ImageUtil {

    public InputStream bufferedImageToInputStream(BufferedImage image, MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new ImageProcessingException("File name is null");
        }

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, extension, outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException ex) {
            log.warn("An error occurred when converting resized image to an InputStream", ex);
            throw new ImageProcessingException("An error occurred when converting resized image to an InputStream", ex);
        }
    }

    public BufferedImage resizeImage(BufferedImage bufferedImage, int maxWidth, int maxHeight) {
        try {
            return Thumbnails.of(bufferedImage)
                    .size(maxWidth, maxHeight)
                    .asBufferedImage();
        } catch (IOException ex) {
            log.warn("An error occurred when resizing image.", ex);
            throw new ImageProcessingException("An error occurred when resizing image.");
        }
    }
}
