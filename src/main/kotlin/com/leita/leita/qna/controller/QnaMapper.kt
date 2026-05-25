package com.leita.leita.qna.controller

import com.leita.leita.qna.domain.Qna
import com.leita.leita.qna.dto.QnaResponse
import com.leita.leita.qna.dto.QnaPageResponse
import org.springframework.data.domain.Page

class QnaMapper {
    companion object {
        fun toQnaResponse(qna: Qna): QnaResponse {
            return QnaResponse(
                id = qna.id,
                title = qna.title,
                content = qna.content,
                authorName = qna.author.name,
                authorEmail = qna.author.email,
                answer = qna.answer,
                answeredAt = qna.answeredAt,
                createdAt = qna.createdAt,
                updatedAt = qna.updatedAt
            )
        }

        fun toQnaPageResponse(page: Page<Qna>): QnaPageResponse {
            return QnaPageResponse(
                content = page.content.map { toQnaResponse(it) },
                currentPage = page.number,
                totalPages = page.totalPages,
                totalElements = page.totalElements,
                size = page.size
            )
        }
    }
}
