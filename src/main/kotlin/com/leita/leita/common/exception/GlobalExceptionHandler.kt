package com.leita.leita.common.exception

import com.leita.leita.port.slack.SlackLabel
import com.leita.leita.port.slack.SlackLogLevel
import com.leita.leita.port.slack.SlackPort
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler(
    private val slackPort: SlackPort
) {
    @ExceptionHandler(CustomException::class)
    fun handleRuntimeException(ex: CustomException): ResponseEntity<ErrorResponse> {

        slackPort.sendMsg(
            LocalDateTime.now(),
            ex.message.orEmpty(),
            SlackLogLevel.ERROR,
            SlackLabel.SYSTEM_ALERT
        )

        val errorResponse = ErrorResponse(ex.message.orEmpty())
        return ResponseEntity(errorResponse, ex.status)
    }
}