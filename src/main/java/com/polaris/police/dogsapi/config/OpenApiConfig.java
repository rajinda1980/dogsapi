package com.polaris.police.dogsapi.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Dog Registration System", version = "1.0", description = "Dogs registered with a police force"))
public class OpenApiConfig {
}
