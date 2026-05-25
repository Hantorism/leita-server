package com.leita.leita.qna.dto

import com.leita.leita.common.dto.BasePage
import java.time.LocalDateTime

data class QnaResponse(
    val id: Long,
    val title: String,
    val content: String,
    val authorName: String,
    val authorEmail: String,
    val answer: String?,
    val answeredAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class QnaPageResponse(
    override val content: List<QnaResponse>,
    override val currentPage: Int,
    override val totalPages: Int,
    override val totalElements: Long,
    override val size: Int
) : BasePage<QnaResponse>
