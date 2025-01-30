package faang.school.postservice.config.thread.pool;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@Data
public class ThreadPoolConfig {

    @Value("${thread-pool.verification-content-pool.num-of-thread}")
    private int verificationPoolNumOfThreads;

    @Value("${thread-pool.core-size}")
    private int corePoolSize;

    @Value("${thread-pool.max-size}")
    private int maxPoolSize;

    public static final String VERIFICATION_POOL_BEAN_NAME = "verificationContentPool";

    @Bean(name = VERIFICATION_POOL_BEAN_NAME)
    public ExecutorService getVerificationContentPool() {
        return Executors.newFixedThreadPool(verificationPoolNumOfThreads);
    }

    @Bean
    public TaskExecutor threadPool() {
        ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
        threadPool.setCorePoolSize(corePoolSize);
        threadPool.setMaxPoolSize(maxPoolSize);
        threadPool.afterPropertiesSet();
        threadPool.initialize();
        return threadPool;
    }
}