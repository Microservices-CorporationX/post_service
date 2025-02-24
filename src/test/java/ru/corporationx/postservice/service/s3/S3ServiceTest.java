package ru.corporationx.postservice.service.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import ru.corporationx.postservice.dto.resource.ResourceInfoDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.corporationx.postservice.service.s3.S3Service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {
    @Mock
    AmazonS3 amazonS3;
    @InjectMocks
    S3Service s3Service;

    @Test
    void testUploadFileOk() {
        Mockito.when(amazonS3.putObject(any())).thenReturn(any());

        ResourceInfoDto file = ResourceInfoDto.builder()
                .name("name")
                .type("image/jpeg")
                .key("key")
                .bytes(new byte[1])
                .build();

        Assertions.assertDoesNotThrow(() -> s3Service.uploadFile(file));
        Mockito.verify(amazonS3, times(1)).putObject(any());
    }

    @Test
    void testUploadFileFail() {
        Mockito.when(amazonS3.putObject(any())).thenThrow(new SdkClientException(""));

        ResourceInfoDto file = ResourceInfoDto.builder()
                .name("name")
                .type("image/jpeg")
                .key("key")
                .bytes(new byte[1])
                .build();

        Assertions.assertThrows(IllegalStateException.class, () -> s3Service.uploadFile(file));
        Mockito.verify(amazonS3, times(1)).putObject(any());
    }

}
