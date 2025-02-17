package com.leita.leita.common.exception

import org.springframework.http.HttpStatus

open class CustomException(
    message: String,
    val status: HttpStatus
) : RuntimeException(message)