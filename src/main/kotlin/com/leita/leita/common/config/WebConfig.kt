package com.leita.leita.common.config

import com.leita.leita.common.LoggingFilter
import com.leita.leita.common.LoggingInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val loggingInterceptor: LoggingInterceptor,
    private val serverConfig: ServerConfig
) : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        val allowedOrigins = serverConfig.allowedOrigins.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        registry.addMapping("/**")
            .allowedOriginPatterns(*allowedOrigins)
            .allowedMethods("*")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3000)
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(loggingInterceptor)
            .addPathPatterns("/**")
            .excludePathPatterns("/v3/**", "/swagger-ui/**", "/swagger-resources/**", "/favicon.ico")
    }
}
