package faang.school.postservice.config.multithreading;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class MultithreadingConfig {
    @Value("${multithreading.write-to-cache.pool-size}")
    private int writeToCachePoolSize;

    @Value("${multithreading.send-events.pool-size}")
    private int sendEventsPoolSize;

    @Value("${multithreading.heat-cache.pool-size}")
    private int heatCachePoolSize;

    @Bean
    public ExecutorService writeToCacheThreadPool() {
        return Executors.newFixedThreadPool(writeToCachePoolSize);
    }

    @Bean
    public ExecutorService sendEventsThreadPool() {
        return Executors.newFixedThreadPool(sendEventsPoolSize);
    }

    @Bean
    public ExecutorService heatCacheThreadPool() {
        return Executors.newFixedThreadPool(heatCachePoolSize);
    }
}
