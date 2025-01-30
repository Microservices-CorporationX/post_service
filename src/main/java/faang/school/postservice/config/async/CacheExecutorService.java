package faang.school.postservice.config.async;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class CacheExecutorService {

    @Value("${cache.executor.pool-size:1}")
    private int poolSize;

    @Bean
    public ExecutorService cacheExecutor() {
        return Executors.newFixedThreadPool(poolSize);
    }
}
