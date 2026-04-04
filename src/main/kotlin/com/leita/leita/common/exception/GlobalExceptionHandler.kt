package com.leita.leita.common.exception

import com.leita.leita.external.slack.SlackLabel
import com.leita.leita.external.slack.SlackLogLevel
import com.leita.leita.external.slack.SlackUtil
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler(
    private val slackUtil: SlackUtil
) {
    @ExceptionHandler(CustomException::class)
    fun handleRuntimeException(ex: CustomException): ResponseEntity<ErrorResponse> {

        slackUtil.sendMsg(
            LocalDateTime.now(),
            ex.message.orEmpty(),
            SlackLogLevel.ERROR,
            SlackLabel.SYSTEM_ALERT
        )

        val errorResponse = ErrorResponse(ex.message.orEmpty())
        return ResponseEntity(errorResponse, ex.status)
    }
}