package com.leita.leita.qna.dto

data class QnaRequest(
    val title: String,
    val content: String
)

data class QnaReplyRequest(
    val answer: String
)
