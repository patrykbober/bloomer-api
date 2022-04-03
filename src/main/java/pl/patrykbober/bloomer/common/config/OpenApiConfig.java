package pl.patrykbober.bloomer.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bloomerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bloomer API")
                        .description("Bloomer backend application")
                        .version("v0.0.1"));
    }

}
