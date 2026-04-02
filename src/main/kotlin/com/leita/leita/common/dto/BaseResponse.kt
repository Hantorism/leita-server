package com.leita.leita.common.dto

data class BaseResponse<T>(
    val message: String,
    val data: T?
)