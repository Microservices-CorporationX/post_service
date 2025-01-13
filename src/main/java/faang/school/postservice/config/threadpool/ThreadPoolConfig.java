package faang.school.postservice.config.threadpool;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.post-task.execution")
public class ThreadPoolConfig {
    @Getter
    @Setter
    public static class Pool {
        private int coreSize;
        private int maxSize;
        private int queueCapacity;
    }

    private String threadNamePrefix;
    private Pool pool;

    @Bean
    public ThreadPoolTaskExecutor postPublishExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(pool.getCoreSize());
        executor.setMaxPoolSize(pool.getMaxSize());
        executor.setQueueCapacity(pool.getQueueCapacity());
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();

        return executor;
    }

}
