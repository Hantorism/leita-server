package com.leita.leita.common.config

import feign.Client
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FeignConfig {
    @Bean
    fun feignClient(): Client = feign.okhttp.OkHttpClient()
}