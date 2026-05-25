package com.leita.leita.notice.dto

import com.leita.leita.common.dto.BasePage
import java.time.LocalDateTime

data class NoticeResponse(
    val id: Long,
    val title: String,
    val content: String,
    val authorName: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class NoticePageResponse(
    override val content: List<NoticeResponse>,
    override val currentPage: Int,
    override val totalPages: Int,
    override val totalElements: Long,
    override val size: Int
) : BasePage<NoticeResponse>
