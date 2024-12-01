package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${api.content-analysis-service.base-url}")
    private String baseUrl;

    @Bean
    public WebClient contentAnalysisWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
