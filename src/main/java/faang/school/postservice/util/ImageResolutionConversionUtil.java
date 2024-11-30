package faang.school.postservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ImageResolutionConversionUtil {

    public List<MultipartFile> imagesListCompression(List<MultipartFile> files) {

        List<MultipartFile> compressedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                BufferedImage image = ImageIO.read(file.getInputStream());
                int width = image.getWidth();
                int height = image.getHeight();

                //добавить проверку на то что это картинка

                if (width > height && (width > 1080 || height > 566)) {
                        image = resizeImage(image, 1080, 566);
                }

                if (width < height && (width > 566 || height > 1080)) {
                        image = resizeImage(image, 566, 1080);
                }

                if (width == height && width > 1080) {
                        image = resizeImage(image, 1080, 1080);
                }

                MultipartFile compressedFile = convertToMultipartFile(image, file.getOriginalFilename());
                compressedFiles.add(compressedFile);

            } catch (IOException e) {
                log.error("Error reading file: {}", e.getMessage(), e);
                throw new IllegalStateException("Cannot read file: ", e);
            }

        }
        return compressedFiles;
    }

    private BufferedImage resizeImage(BufferedImage image, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, image.getType());
        resizedImage.getGraphics().drawImage(image.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH), 0, 0, null);
        return resizedImage;
    }

}
