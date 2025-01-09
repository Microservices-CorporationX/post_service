package faang.school.postservice.config.comment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommentModerationConfig {
    @Value("${moderation.thread-pool-size}")
    private int poolSize;

    @Bean
    public ExecutorService moderationCommentThreadPool() {
        return Executors.newFixedThreadPool(poolSize);
    }
}
