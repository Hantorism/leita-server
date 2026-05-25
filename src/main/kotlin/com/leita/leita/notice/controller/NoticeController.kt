package com.leita.leita.notice.controller

import com.leita.leita.common.dto.BaseResponse
import com.leita.leita.notice.dto.NoticeRequest
import com.leita.leita.notice.dto.NoticeResponse
import com.leita.leita.notice.dto.NoticePageResponse
import com.leita.leita.notice.service.NoticeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class NoticeController(private val noticeService: NoticeService) {

    // User & Admin: Get paginated notices
    @GetMapping("/notices")
    fun getNotices(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<BaseResponse<NoticePageResponse>> {
        val notices = noticeService.getNotices(page, size)
        val response = NoticeMapper.toNoticePageResponse(notices)
        return ResponseEntity.ok(BaseResponse("공지사항 목록 조회 완료", response))
    }

    // User & Admin: Get notice detail
    @GetMapping("/notices/{id}")
    fun getNotice(@PathVariable id: Long): ResponseEntity<BaseResponse<NoticeResponse>> {
        val notice = noticeService.getNotice(id)
        val response = NoticeMapper.toNoticeResponse(notice)
        return ResponseEntity.ok(BaseResponse("공지사항 상세 조회 완료", response))
    }

    // Admin: Get paginated notices (same as above but prefix /admin)
    @GetMapping("/admin/notices")
    fun getAdminNotices(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<BaseResponse<NoticePageResponse>> {
        val notices = noticeService.getNotices(page, size)
        val response = NoticeMapper.toNoticePageResponse(notices)
        return ResponseEntity.ok(BaseResponse("어드민 공지사항 목록 조회 완료", response))
    }

    // Admin: Get notice detail
    @GetMapping("/admin/notices/{id}")
    fun getAdminNotice(@PathVariable id: Long): ResponseEntity<BaseResponse<NoticeResponse>> {
        val notice = noticeService.getNotice(id)
        val response = NoticeMapper.toNoticeResponse(notice)
        return ResponseEntity.ok(BaseResponse("어드민 공지사항 상세 조회 완료", response))
    }

    // Admin: Create notice
    @PostMapping("/admin/notices")
    fun createNotice(@RequestBody request: NoticeRequest): ResponseEntity<BaseResponse<NoticeResponse>> {
        val notice = noticeService.createNotice(request)
        val response = NoticeMapper.toNoticeResponse(notice)
        return ResponseEntity.ok(BaseResponse("공지사항 생성 완료", response))
    }

    // Admin: Update notice
    @PutMapping("/admin/notices/{id}")
    fun updateNotice(
        @PathVariable id: Long,
        @RequestBody request: NoticeRequest
    ): ResponseEntity<BaseResponse<NoticeResponse>> {
        val notice = noticeService.updateNotice(id, request)
        val response = NoticeMapper.toNoticeResponse(notice)
        return ResponseEntity.ok(BaseResponse("공지사항 수정 완료", response))
    }

    // Admin: Delete notice
    @DeleteMapping("/admin/notices/{id}")
    fun deleteNotice(@PathVariable id: Long): ResponseEntity<BaseResponse<Unit>> {
        noticeService.deleteNotice(id)
        return ResponseEntity.ok(BaseResponse("공지사항 삭제 완료", Unit))
    }
}
