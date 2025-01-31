package faang.school.postservice.config.post;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class PostCorrectorConfig {
    @Value("${post-correction.thread-pool-size}")
    private int fixedThreadPoolSize;

    @Bean
    public ExecutorService postCorrectorThreadPool() {
        return Executors.newFixedThreadPool(fixedThreadPoolSize);
    }
}
