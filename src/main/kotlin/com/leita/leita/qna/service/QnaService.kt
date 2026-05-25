package com.leita.leita.qna.service

import com.leita.leita.common.exception.CustomException
import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.qna.domain.Qna
import com.leita.leita.qna.dto.QnaRequest
import com.leita.leita.qna.dto.QnaReplyRequest
import com.leita.leita.qna.repository.QnaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class QnaService(
    private val qnaRepository: QnaRepository,
    private val jwtUtils: JwtUtils
) {
    fun getAllQnas(page: Int, size: Int): Page<Qna> {
        val pageable = PageRequest.of(page, size, Sort.by("id").descending())
        return qnaRepository.findAll(pageable)
    }

    fun getMyQnas(page: Int, size: Int): Page<Qna> {
        val user = jwtUtils.extractUser()
        val pageable = PageRequest.of(page, size, Sort.by("id").descending())
        return qnaRepository.findByAuthor(user, pageable)
    }

    fun getQna(id: Long): Qna {
        return qnaRepository.findById(id).orElseThrow {
            CustomException("QnA not found with id: $id", HttpStatus.NOT_FOUND)
        }
    }

    @Transactional
    fun createQna(request: QnaRequest): Qna {
        val user = jwtUtils.extractUser()
        val qna = Qna(
            title = request.title,
            content = request.content,
            author = user
        )
        return qnaRepository.save(qna)
    }

    @Transactional
    fun updateQna(id: Long, request: QnaRequest): Qna {
        val user = jwtUtils.extractUser()
        val qna = getQna(id)
        if (qna.author.id != user.id) {
            throw CustomException("You are not the author of this QnA", HttpStatus.FORBIDDEN)
        }
        qna.update(request.title, request.content)
        return qnaRepository.save(qna)
    }

    @Transactional
    fun deleteQna(id: Long) {
        val user = jwtUtils.extractUser()
        val qna = getQna(id)
        if (qna.author.id != user.id) {
            throw CustomException("You are not the author of this QnA", HttpStatus.FORBIDDEN)
        }
        qnaRepository.delete(qna)
    }

    @Transactional
    fun replyQna(id: Long, request: QnaReplyRequest): Qna {
        val qna = getQna(id)
        qna.reply(request.answer)
        return qnaRepository.save(qna)
    }
}
