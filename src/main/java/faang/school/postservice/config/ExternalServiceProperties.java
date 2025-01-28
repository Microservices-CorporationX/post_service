package faang.school.postservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "external")
@Getter
@Setter
public class ExternalServiceProperties {
    private String userServiceUrl;
    private String projectServiceUrl;

}
