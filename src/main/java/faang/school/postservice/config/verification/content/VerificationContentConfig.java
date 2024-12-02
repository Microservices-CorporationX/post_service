package faang.school.postservice.config.verification.content;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@Data
public class VerificationContentConfig {

    @Value("${thread-pool.verification-content-pool.num-of-thread}")
    private int numOfThreads;

    public static final String THREAD_POOL_BEAN_NAME = "verificationContentPool";

    @Bean(name = THREAD_POOL_BEAN_NAME)

    public ExecutorService getThreadPool() {
        return Executors.newFixedThreadPool(numOfThreads);
    }
}
