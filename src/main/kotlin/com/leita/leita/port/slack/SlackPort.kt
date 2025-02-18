package com.leita.leita.port.slack

import java.time.LocalDateTime

interface SlackPort {
    fun sendMsg(
        timestamp: LocalDateTime,
        description: String,
        logLevel: SlackLogLevel,
        label: SlackLabel
    ): Boolean
}