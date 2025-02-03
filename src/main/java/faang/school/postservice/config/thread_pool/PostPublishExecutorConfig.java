package faang.school.postservice.config.thread_pool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class PostPublishExecutorConfig {

    @Value("${task-executor.post-publish.core-pool-size}")
    private int corePoolSize;

    @Value("${task-executor.post-publish.max-pool-size}")
    private int maxPoolSize;

    @Value("${task-executor.post-publish.queue-capacity}")
    private int queueCapacity;

    @Bean
    public Executor threadPoolExecutorForPublishingPosts() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.initialize();
        return executor;
    }
}
