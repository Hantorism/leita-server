package com.leita.leita.common.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "server.servlet")
data class ServerConfig(
    var allowedOrigins: String = "*",
    val contextPath: String = "/api"
)