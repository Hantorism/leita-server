package com.leita.leita.controller.dto

data class BaseResponse<T>(
    val message: String,

    val data: T?
)