package com.leita.leita.judge.dto

data class SubmitWCRequest(
    val submitId: Long,
    val code: String,
    val language: String,
    val limit: LimitWCRequest
)
