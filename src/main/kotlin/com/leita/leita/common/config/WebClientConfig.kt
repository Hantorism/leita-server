package com.leita.leita.common.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {

    @Value("\${external-server.judge}")
    var judgeBaseUrl: String = ""

    @Bean
    fun webClient(): WebClient {
        return WebClient.builder().build();
    }
}