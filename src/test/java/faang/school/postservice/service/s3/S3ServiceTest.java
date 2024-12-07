package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.postservice.exception.FileException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {
    @InjectMocks
    private S3Service s3Service;

    @Mock
    private AmazonS3 amazonS3;
    private String bucket = "testBucket";
    private MultipartFile multipartFile;
    private String key = "key";
    private Post post;

    @BeforeEach
    void setUp() {
        multipartFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "test content".getBytes());
        ReflectionTestUtils.setField(s3Service, "bucketName", bucket);

        post = new Post();
        post.setAuthorId(1L);
        post.setId(1L);
    }

    @Test
    void testAddResource() {
       s3Service.uploadResource(multipartFile, key);

        verify(amazonS3).putObject(any(PutObjectRequest.class));
    }

    @Test
    void testUpdateResource() {
        s3Service.updateResource(multipartFile, key);

        verify(amazonS3).deleteObject(bucket, key);
        verify(amazonS3).putObject(any(PutObjectRequest.class));
    }

    @Test
    void testCompleteRemoval() {
        s3Service.completeRemoval(key);

        verify(amazonS3).deleteObject(bucket, key);
    }

    @Test
    void testDownloadResourceException() {
        assertThrows(FileException.class,
                () ->  s3Service.downloadResource(key));
    }

    @Test
    void testDownloadResource() throws IOException {
        when(amazonS3.getObject(any(String.class), any(String.class))).thenReturn(new S3Object());

        s3Service.downloadResource(key);

        verify(amazonS3, times(1)).getObject(bucket, key);
    }

    @Test
    void testGeneratePresignedUrl() {
        String expectedUrl = "http://key";
        URL url;
        try {
            url = new URL(expectedUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        when(amazonS3.generatePresignedUrl(any(GeneratePresignedUrlRequest.class)))
                .thenReturn(url);

        String result = s3Service.generatePresignedUrl(key);

        assertEquals(result, expectedUrl);
    }
}
