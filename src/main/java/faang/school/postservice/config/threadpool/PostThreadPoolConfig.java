package faang.school.postservice.config.threadpool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Configuration
public class PostThreadPoolConfig {

    @Bean(destroyMethod = "shutdown")
    public ExecutorService executorService() {
        log.info("Thread pool created");

        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }
}
