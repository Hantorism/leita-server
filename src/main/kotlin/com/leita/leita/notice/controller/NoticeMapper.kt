package com.leita.leita.notice.controller

import com.leita.leita.notice.domain.Notice
import com.leita.leita.notice.dto.NoticeResponse
import com.leita.leita.notice.dto.NoticePageResponse
import org.springframework.data.domain.Page

class NoticeMapper {
    companion object {
        fun toNoticeResponse(notice: Notice): NoticeResponse {
            return NoticeResponse(
                id = notice.id,
                title = notice.title,
                content = notice.content,
                authorName = notice.authorName,
                createdAt = notice.createdAt,
                updatedAt = notice.updatedAt
            )
        }

        fun toNoticePageResponse(page: Page<Notice>): NoticePageResponse {
            return NoticePageResponse(
                content = page.content.map { toNoticeResponse(it) },
                currentPage = page.number,
                totalPages = page.totalPages,
                totalElements = page.totalElements,
                size = page.size
            )
        }
    }
}
