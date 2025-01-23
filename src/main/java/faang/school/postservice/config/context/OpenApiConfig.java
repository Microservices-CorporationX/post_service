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
                        .title("PostService Open API")
                        .version("2.2.0")
                        .description("Swagger Tools"));
    }
}