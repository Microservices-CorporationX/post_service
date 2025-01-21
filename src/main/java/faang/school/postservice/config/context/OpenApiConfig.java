package faang.school.postservice.config.context;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI postServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .description("PostService API"));
    }
}
