package com.leita.leita.util.slack

import com.leita.leita.common.config.env.SpringEnv
import com.leita.leita.common.exception.CustomException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class SlackUtil(
    private val springEnv: SpringEnv,
    private val slackClient: SlackClient,
) {

    fun sendMsg(
        timestamp: LocalDateTime,
        description: String,
        logLevel: SlackLogLevel,
        label: SlackLabel
    ) {
        try {
            if(!springEnv.isLocalProfile()) {
                val formattedMsg = createMessage(
                    timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    label.title, description, logLevel, label
                )
                slackClient.sendMessage(formattedMsg)
            }
        } catch (e: Exception) {
            throw CustomException("An error occurred while sending the message to Slack: $e", HttpStatus.INTERNAL_SERVER_ERROR)
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

