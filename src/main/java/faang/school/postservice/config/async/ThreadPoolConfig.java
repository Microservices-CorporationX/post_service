package faang.school.postservice.config.async;

import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class ThreadPoolConfig {
    @Value("${async.core-pool-size}")
    private int corePoolSize;

    @Value("${async.max-pool-size}")
    private int maxPoolSize;

    @Value("${async.queue-capacity}")
    private int queueCapacity;

    @Value("${async.thread-name-prefix}")
    private String threadNamePrefix;

    private ThreadPoolTaskExecutor executor;

    @Bean(name = "doniyorTaskExecutor")
    public TaskExecutor taskExecutor() {
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }

    @PreDestroy
    public void shutdownExecutor() {
        if (executor != null) {
            executor.shutdown();
        }
    }
}
