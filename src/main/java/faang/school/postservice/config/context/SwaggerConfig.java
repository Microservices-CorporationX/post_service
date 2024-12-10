package faang.school.postservice.config.context;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Contact;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Post service API")
                        .version("1.0.0")
                        .description("Post service API")
                        .contact(new Contact()
                                .name("CorporationX")
                                .email("corpX.bc.com")));
    }

}
