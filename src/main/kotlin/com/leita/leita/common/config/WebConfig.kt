package com.leita.leita.common.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@ConfigurationProperties(prefix = "server.servlet")
class WebConfig : WebMvcConfigurer {

    var allowedOrigins: String = "*"
    val contextPath: String = "/api"

    override fun addCorsMappings(registry: CorsRegistry) {
        val allowedOrigins = allowedOrigins.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        registry.addMapping("/**")
            .allowedOriginPatterns(*allowedOrigins)
            .allowedMethods("*")
            .allowedHeaders("*")
//            TODO: 개발 기간 임시
//            .allowCredentials(true)
            .maxAge(3000)
    }
}
