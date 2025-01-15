package com.leita.leita.common.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders

@Configuration
class SwaggerConfig {
    @Bean
    fun customOpenAPI(): OpenAPI {
        val info = Info()
            .title("Leita API")
            .version("1.0.0")
            .description("Leita API Docs")

        val bearerAuth = SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .`in`(SecurityScheme.In.HEADER)
            .name(HttpHeaders.AUTHORIZATION)

        val addSecurityItem = SecurityRequirement()
        addSecurityItem.addList("JWT")

        val components = Components()
        components.addSecuritySchemes("JWT", bearerAuth)

        return OpenAPI()
            .components(components)
            .addSecurityItem(addSecurityItem)
            .info(info)
    }
}
