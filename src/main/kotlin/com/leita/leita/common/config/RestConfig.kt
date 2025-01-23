package com.leita.leita.common.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
@ConfigurationProperties(prefix = "external-server")
class RestConfig(
    var judge: String = ""
) {
    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
}