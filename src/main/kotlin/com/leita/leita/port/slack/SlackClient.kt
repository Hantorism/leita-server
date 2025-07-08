package com.leita.leita.port.slack

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping

@FeignClient(name = "slackClient", url = "\${external-server.slack}")
interface SlackClient {
    @PostMapping
    fun sendMessage(request: String): String
}