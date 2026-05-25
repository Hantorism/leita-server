package com.leita.leita.notice.service

import com.leita.leita.common.exception.CustomException
import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.notice.domain.Notice
import com.leita.leita.notice.dto.NoticeRequest
import com.leita.leita.notice.repository.NoticeRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class NoticeService(
    private val noticeRepository: NoticeRepository,
    private val jwtUtils: JwtUtils
) {
    fun getNotices(page: Int, size: Int): Page<Notice> {
        val pageable = PageRequest.of(page, size, Sort.by("id").descending())
        return noticeRepository.findAll(pageable)
    }

    fun getNotice(id: Long): Notice {
        return noticeRepository.findById(id).orElseThrow {
            CustomException("Notice not found with id: $id", HttpStatus.NOT_FOUND)
        }
    }

    @Transactional
    fun createNotice(request: NoticeRequest): Notice {
        val user = jwtUtils.extractUser()
        val notice = Notice(
            title = request.title,
            content = request.content,
            authorName = user.name
        )
        return noticeRepository.save(notice)
    }

    @Transactional
    fun updateNotice(id: Long, request: NoticeRequest): Notice {
        val notice = getNotice(id)
        notice.update(request.title, request.content)
        return noticeRepository.save(notice)
    }

    @Transactional
    fun deleteNotice(id: Long) {
        val notice = getNotice(id)
        noticeRepository.delete(notice)
    }
}
