package faang.school.postservice.config.threadpool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {
    @Bean(name = "executorKafkaSend")
    public ExecutorService executorPostPublishedEvent() {
        return Executors.newCachedThreadPool();
    }
}
