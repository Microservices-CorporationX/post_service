package faang.school.postservice.config.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class UserBannerConfig {

    @Value("${banner.thread-pool}")
    private int userBannerTreadPool;

    @Bean
    public ExecutorService userBannerThreadPool() {
        return Executors.newFixedThreadPool(userBannerTreadPool);
    }
}
