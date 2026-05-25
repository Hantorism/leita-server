package com.leita.leita.qna.controller

import com.leita.leita.common.dto.BaseResponse
import com.leita.leita.qna.dto.QnaRequest
import com.leita.leita.qna.dto.QnaReplyRequest
import com.leita.leita.qna.dto.QnaResponse
import com.leita.leita.qna.dto.QnaPageResponse
import com.leita.leita.qna.service.QnaService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class QnaController(private val qnaService: QnaService) {

    // User & Admin: Get all QnAs
    @GetMapping("/qna")
    fun getAllQnas(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<BaseResponse<QnaPageResponse>> {
        val qnas = qnaService.getAllQnas(page, size)
        val response = QnaMapper.toQnaPageResponse(qnas)
        return ResponseEntity.ok(BaseResponse("QnA 목록 조회 완료", response))
    }

    // User: Get own QnAs
    @GetMapping("/qna/my")
    fun getMyQnas(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<BaseResponse<QnaPageResponse>> {
        val qnas = qnaService.getMyQnas(page, size)
        val response = QnaMapper.toQnaPageResponse(qnas)
        return ResponseEntity.ok(BaseResponse("내 QnA 목록 조회 완료", response))
    }

    // User & Admin: Get QnA detail
    @GetMapping("/qna/{id}")
    fun getQna(@PathVariable id: Long): ResponseEntity<BaseResponse<QnaResponse>> {
        val qna = qnaService.getQna(id)
        val response = QnaMapper.toQnaResponse(qna)
        return ResponseEntity.ok(BaseResponse("QnA 상세 조회 완료", response))
    }

    // User: Create QnA
    @PostMapping("/qna")
    fun createQna(@RequestBody request: QnaRequest): ResponseEntity<BaseResponse<QnaResponse>> {
        val qna = qnaService.createQna(request)
        val response = QnaMapper.toQnaResponse(qna)
        return ResponseEntity.ok(BaseResponse("QnA 작성 완료", response))
    }

    // User: Update QnA
    @PutMapping("/qna/{id}")
    fun updateQna(
        @PathVariable id: Long,
        @RequestBody request: QnaRequest
    ): ResponseEntity<BaseResponse<QnaResponse>> {
        val qna = qnaService.updateQna(id, request)
        val response = QnaMapper.toQnaResponse(qna)
        return ResponseEntity.ok(BaseResponse("QnA 수정 완료", response))
    }

    // User: Delete QnA
    @DeleteMapping("/qna/{id}")
    fun deleteQna(@PathVariable id: Long): ResponseEntity<BaseResponse<Unit>> {
        qnaService.deleteQna(id)
        return ResponseEntity.ok(BaseResponse("QnA 삭제 완료", Unit))
    }

    // Admin: List QnAs
    @GetMapping("/admin/qna")
    fun getAdminQnas(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<BaseResponse<QnaPageResponse>> {
        val qnas = qnaService.getAllQnas(page, size)
        val response = QnaMapper.toQnaPageResponse(qnas)
        return ResponseEntity.ok(BaseResponse("어드민 QnA 목록 조회 완료", response))
    }

    // Admin: Get QnA detail
    @GetMapping("/admin/qna/{id}")
    fun getAdminQna(@PathVariable id: Long): ResponseEntity<BaseResponse<QnaResponse>> {
        val qna = qnaService.getQna(id)
        val response = QnaMapper.toQnaResponse(qna)
        return ResponseEntity.ok(BaseResponse("어드민 QnA 상세 조회 완료", response))
    }

    // Admin: Reply to QnA
    @PostMapping("/admin/qna/{id}/reply")
    fun replyQna(
        @PathVariable id: Long,
        @RequestBody request: QnaReplyRequest
    ): ResponseEntity<BaseResponse<QnaResponse>> {
        val qna = qnaService.replyQna(id, request)
        val response = QnaMapper.toQnaResponse(qna)
        return ResponseEntity.ok(BaseResponse("QnA 답변 등록 완료", response))
    }
}
