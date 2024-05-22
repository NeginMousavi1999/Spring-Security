package com.example.springsecurity.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "app.api")
@Getter
@Setter
public class SpringFoxConfiguration {

    private String version;
    private String title;
    private String description;
    private String basePackage;

    @Bean
    public OpenAPI example() {
        return new OpenAPI()
                .info(new Info().title(title)
                        .description(description)
                        .version(version)
                );
    }
}
