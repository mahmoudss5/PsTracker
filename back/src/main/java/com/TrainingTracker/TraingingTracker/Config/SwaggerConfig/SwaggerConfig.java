package com.TrainingTracker.TraingingTracker.Config.SwaggerConfig;
import com.TrainingTracker.TraingingTracker.Config.AppProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {

        @Bean
        public OpenAPI openAPI(AppProperties appProperties) {
                return new OpenAPI()
                        .info(new Info()
                                .contact(new Contact()
                                        .name("Mahmoud")
                                        .email("contact@mahmoud.com")
                                        .url("https://mahmoud.com"))
                                .description("OpenApi documentation for Spring Security")
                                .title("OpenApi specification - Mahmoud")
                                .version("1.0")
                                .license(new License()
                                        .name("Licence name")
                                        .url("https://some-url.com"))
                                .termsOfService("Terms of service"))
                        .addServersItem(new Server()
                                .description("Local ENV")
                                .url(appProperties.swaggerServerUrl()));
        }
}