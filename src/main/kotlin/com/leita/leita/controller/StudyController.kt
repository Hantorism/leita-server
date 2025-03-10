package com.leita.leita.controller

import com.leita.leita.controller.dto.BaseResponse
import com.leita.leita.controller.dto.problem.response.StudiesResponse
import com.leita.leita.controller.dto.study.request.StudyCreateRequest
import com.leita.leita.controller.dto.study.request.StudyRoleChangeRequest
import com.leita.leita.controller.dto.study.response.*
import com.leita.leita.service.StudyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/study")
class StudyController(private val studyService: StudyService) {

    @GetMapping
    fun getStudies(
        @RequestParam page: Int = 0, @RequestParam size: Int = 10
    ): ResponseEntity<BaseResponse<StudiesResponse>> {
        val response = studyService.getStudies(page, size)
        val wrappedResponse: BaseResponse<StudiesResponse> = BaseResponse("스터디 조회 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @GetMapping("/{id}")
    fun getStudy(@PathVariable id: Long): ResponseEntity<BaseResponse<StudyDetailResponse>> {
        val response = studyService.getStudy(id)
        val wrappedResponse: BaseResponse<StudyDetailResponse> = BaseResponse("스터디 조회 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @PostMapping
    fun createStudy(@RequestBody request: StudyCreateRequest): ResponseEntity<BaseResponse<StudyCreateResponse>> {
        val response = studyService.create(request)
        val wrappedResponse: BaseResponse<StudyCreateResponse> = BaseResponse("스터디 생성 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @GetMapping("/{id}/members")
    fun getStudyMembers(
        @PathVariable id: Long, @RequestParam page: Int = 0, @RequestParam size: Int = 10
    ): ResponseEntity<BaseResponse<StudyPendingResponse>> {
        val response = studyService.getStudyMembers(id, page, size)
        val wrappedResponse: BaseResponse<StudyPendingResponse> = BaseResponse("스터디 멤버 조회 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    // 관리자
    @PostMapping("/{id}/role-change")
    fun pending(
        @PathVariable id: Long, @RequestBody request: StudyRoleChangeRequest
    ): ResponseEntity<BaseResponse<StudyRoleChangeResponse>> {
        val response = studyService.changeRole(id, request)
        val wrappedResponse: BaseResponse<StudyRoleChangeResponse> = BaseResponse("스터디 역할 변경 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @GetMapping("/{id}/pending")
    fun pending(
        @PathVariable id: Long, @RequestParam page: Int = 0, @RequestParam size: Int = 10
    ): ResponseEntity<BaseResponse<StudyPendingResponse>> {
        val response = studyService.pending(id, page, size)
        val wrappedResponse: BaseResponse<StudyPendingResponse> = BaseResponse("스터디 참가 조회 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @GetMapping("/{id}/approve")
    fun approve(@PathVariable id: Long, @RequestParam email: String): ResponseEntity<BaseResponse<StudyApproveResponse>> {
        val response = studyService.approve(id, email)
        val wrappedResponse: BaseResponse<StudyApproveResponse> = BaseResponse("스터디 참가 허용 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @GetMapping("/{id}/deny")
    fun deny(@PathVariable id: Long, @RequestParam email: String): ResponseEntity<BaseResponse<StudyDenyResponse>> {
        val response = studyService.deny(id, email)
        val wrappedResponse: BaseResponse<StudyDenyResponse> = BaseResponse("스터디 참가 거절 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    // 수강생
    @GetMapping("/{id}/join")
    fun join(@PathVariable id: Long): ResponseEntity<BaseResponse<StudyJoinResponse>> {
        val response = studyService.join(id)
        val wrappedResponse: BaseResponse<StudyJoinResponse> = BaseResponse("스터디 참가 신청 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @GetMapping("/{id}/leave")
    fun approve(@PathVariable id: Long): ResponseEntity<BaseResponse<StudyLeaveResponse>> {
        val response = studyService.leave(id)
        val wrappedResponse: BaseResponse<StudyLeaveResponse> = BaseResponse("스터디 나가기 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }
}