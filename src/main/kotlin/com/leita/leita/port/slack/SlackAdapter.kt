package com.leita.leita.port.slack

import com.leita.leita.common.config.RestConfig
import com.leita.leita.common.config.env.SpringEnv
import com.leita.leita.common.exception.CustomException
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class SlackAdapter(
    private val restConfig: RestConfig,
    private val webClient: WebClient,
    private val springEnv: SpringEnv,
) : SlackPort {

    override fun sendMsg(
        timestamp: LocalDateTime,
        description: String,
        logLevel: SlackLogLevel,
        label: SlackLabel
    ): Boolean {
        try {
            if(springEnv.isLocalProfile()) {
                return false
            }

            val formattedMsg = createMessage(
                timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                label.title, description, logLevel, label
            )

            webClient.post()
                .uri(restConfig.slack)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(formattedMsg)
                .retrieve()
                .bodyToMono(String::class.java)
                .block()!!

            return true
        } catch (e: Exception) {
            throw CustomException("An error occurred while sending the message to Slack", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    private fun createMessage(
        timestamp: String,
        title: String,
        description: String,
        logLevel: SlackLogLevel,
        label: SlackLabel
    ): String {
        return """
            { "blocks": [
                { "type": "header", "text": { "type": "plain_text", "text": "${label.icon} $title" } },
                { "type": "section", "fields": [
                    { "type": "mrkdwn", "text": "*로그 레벨*\n[${logLevel.name}] ${label.name}" },
                    { "type": "mrkdwn", "text": "*발생 시간*\n$timestamp" },
                    { "type": "mrkdwn", "text": "*발생 환경*\n${springEnv.getProfile()}" }
                ] },
                { "type": "divider" },
                { "type": "section", "text": { "type": "mrkdwn", "text": "*상세 내용*\n```$description```" } }
            ] }
        """.trimIndent()
    }
}