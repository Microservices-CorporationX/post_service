package faang.school.postservice.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "Aws.S3")
@Getter
public class AwsS3ApiConfig {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucket;
    private int maxConcurrency;
    private Duration connectionTimeout;
    private Duration readTimeout;
    private Duration writeTimeout;
    private Duration apiCallTimeout;
    private Duration apiCallAttemptTimeout;
}
