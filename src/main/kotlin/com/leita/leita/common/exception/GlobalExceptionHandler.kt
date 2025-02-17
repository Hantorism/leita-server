package com.leita.leita.common.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler

class GlobalExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(CustomException::class)
    fun handleRuntimeException(ex: CustomException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(ex.message.orEmpty())
        return ResponseEntity(errorResponse, ex.status)
    }
}