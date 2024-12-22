package faang.school.postservice.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("Post service API")
                        .version("1.0")
                        .description("API for managing posts")
                        .termsOfService("http://swagger.io/terms/")
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .name("Gadzhi")
                                .email("Gadzhiev3222@example.com")));
    }
}
