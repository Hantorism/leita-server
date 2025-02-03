package com.leita.leita.common.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import java.util.List

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

        val servers = List.of(
            Server().url("https://dev-server.leita.dev/api").description("Dev Server"),
            Server().url("https://server.leita.dev/api").description("Prod Server"),
            Server().url("http://localhost:8080/api").description("Local Server")
        )

        val addSecurityItem = SecurityRequirement()
        addSecurityItem.addList("JWT")

        val components = Components()
        components.addSecuritySchemes("JWT", bearerAuth)

        return OpenAPI()
            .servers(servers)
            .components(components)
            .addSecurityItem(addSecurityItem)
            .info(info)
    }
}
