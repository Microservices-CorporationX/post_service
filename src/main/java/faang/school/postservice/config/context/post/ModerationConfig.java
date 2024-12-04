package faang.school.postservice.config.context.post;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ModerationConfig {

    @Value("${moderation.thread-pool}")
    private int fixedThreadPool;

    @Bean
    public ExecutorService moderationPostThreadPool() {
        return Executors.newFixedThreadPool(fixedThreadPool);
    }
}
