package faang.school.postservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.postservice.util.ImageUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {

    @Mock
    private AmazonS3 s3Client;

    @Mock
    private ImageUtil imageUtil;

    @Mock
    private MultipartFile file;

    @Value("${services.s3.bucketName}")
    private String bucket;

    private final String key = "key";

    @InjectMocks
    private S3Service s3Service;

    @Test
    public void testUploadImageFile() {
        // arrange
        String folder = "test-folder";
        String originalName = "test-image.png";
        String contentType = "image/png";
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        InputStream inputStream = new ByteArrayInputStream(new byte[0]);

        when(file.getOriginalFilename()).thenReturn(originalName);
        when(file.getContentType()).thenReturn(contentType);
        when(imageUtil.bufferedImageToInputStream(image, file)).thenReturn(inputStream);

        // act
        String key = s3Service.uploadImageFile(file, folder, image);

        // assert
        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client, times(1)).putObject(captor.capture());
        PutObjectRequest putObjectRequest = captor.getValue();

        // Тут я получаю миллисекунды которые использовались в качестве названия в методе uploadImageFile
        Pattern pattern = Pattern.compile(String.format("%s/(\\d+):%s", folder, originalName));
        Matcher matcher = pattern.matcher(putObjectRequest.getKey());
        long timestamp = 0;
        if (matcher.find()) {
            timestamp = Long.parseLong(matcher.group(1));
        }

        // Основные проверки
        assertEquals(String.format("%s/%d:%s", folder, timestamp, originalName), putObjectRequest.getKey());
        assertEquals(String.format("%s/%d:%s", folder, timestamp, originalName), key);
        assertEquals(inputStream, putObjectRequest.getInputStream());
        assertEquals(contentType, putObjectRequest.getMetadata().getContentType());
    }

    @Test
    public void testDeleteResource() {
        String key = "test-key";

        s3Service.deleteResource(key);

        verify(s3Client, times(1)).deleteObject(bucket, key);
    }
}