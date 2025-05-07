package com.leita.leita.controller

import com.leita.leita.controller.dto.BaseResponse
import com.leita.leita.controller.dto.studyClass.request.StudyClassCreateRequest
import com.leita.leita.controller.dto.studyClass.request.StudyClassRoleChangeRequest
import com.leita.leita.controller.dto.studyClass.request.StudyClassUpdateRequest
import com.leita.leita.controller.dto.studyClass.response.*
import com.leita.leita.service.StudyClassService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/study-class")
class StudyClassController(private val studyClassService: StudyClassService) {

    @GetMapping
    fun getStudyClasses(
        @RequestParam page: Int = 0, @RequestParam size: Int = 10
    ): ResponseEntity<BaseResponse<StudyClassesResponse>> {
        val response = studyClassService.getStudyClasses(page, size)
        val wrappedResponse: BaseResponse<StudyClassesResponse> = BaseResponse("스터디 조회 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @GetMapping("/{id}")
    fun getStudyClass(@PathVariable id: Long): ResponseEntity<BaseResponse<StudyClassDetailResponse>> {
        val response = studyClassService.getStudyClass(id)
        val wrappedResponse: BaseResponse<StudyClassDetailResponse> = BaseResponse("스터디 조회 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @PutMapping("/{id}")
    fun updateStudyClass(@PathVariable id: Long, request: StudyClassUpdateRequest ): ResponseEntity<BaseResponse<StudyClassDetailResponse>> {
        val response = studyClassService.updateStudyClass(id, request)
        val wrappedResponse: BaseResponse<StudyClassDetailResponse> = BaseResponse("스터디 수정 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @DeleteMapping("/{id}")
    fun deleteStudyClass(@PathVariable id: Long): ResponseEntity<BaseResponse<Void>> {
        studyClassService.deleteStudyClass(id)
        val wrappedResponse: BaseResponse<Void> = BaseResponse("스터디 삭제 완료", null)
        return ResponseEntity.ok(wrappedResponse)
    }

    @PostMapping
    fun createStudyClass(@RequestBody request: StudyClassCreateRequest): ResponseEntity<BaseResponse<StudyClassCreateResponse>> {
        val response = studyClassService.create(request)
        val wrappedResponse: BaseResponse<StudyClassCreateResponse> = BaseResponse("스터디 생성 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @GetMapping("/{id}/members")
    fun getStudyClassMembers(
        @PathVariable id: Long, @RequestParam page: Int = 0, @RequestParam size: Int = 10
    ): ResponseEntity<BaseResponse<StudyClassPendingResponse>> {
        val response = studyClassService.getStudyMembers(id, page, size)
        val wrappedResponse: BaseResponse<StudyClassPendingResponse> = BaseResponse("스터디 멤버 조회 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @PostMapping("/{id}/role-change")
    fun changeRole(
        @PathVariable id: Long, @RequestBody request: StudyClassRoleChangeRequest
    ): ResponseEntity<BaseResponse<StudyClassRoleChangeResponse>> {
        val response = studyClassService.changeRole(id, request)
        val wrappedResponse: BaseResponse<StudyClassRoleChangeResponse> = BaseResponse("스터디 역할 변경 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @GetMapping("/{id}/pendings")
    fun pending(
        @PathVariable id: Long, @RequestParam page: Int = 0, @RequestParam size: Int = 10
    ): ResponseEntity<BaseResponse<StudyClassPendingResponse>> {
        val response = studyClassService.pending(id, page, size)
        val wrappedResponse: BaseResponse<StudyClassPendingResponse> = BaseResponse("스터디 참가 조회 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @GetMapping("/{id}/approve")
    fun approve(@PathVariable id: Long, @RequestParam email: String): ResponseEntity<BaseResponse<StudyClassApproveResponse>> {
        val response = studyClassService.approve(id, email)
        val wrappedResponse: BaseResponse<StudyClassApproveResponse> = BaseResponse("스터디 참가 허용 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @GetMapping("/{id}/deny")
    fun deny(@PathVariable id: Long, @RequestParam email: String): ResponseEntity<BaseResponse<StudyClassDenyResponse>> {
        val response = studyClassService.deny(id, email)
        val wrappedResponse: BaseResponse<StudyClassDenyResponse> = BaseResponse("스터디 참가 거절 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @GetMapping("/{id}/join")
    fun join(@PathVariable id: Long): ResponseEntity<BaseResponse<StudyClassJoinResponse>> {
        val response = studyClassService.join(id)
        val wrappedResponse: BaseResponse<StudyClassJoinResponse> = BaseResponse("스터디 참가 신청 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @GetMapping("/{id}/leave")
    fun approve(@PathVariable id: Long): ResponseEntity<BaseResponse<StudyClassLeaveResponse>> {
        val response = studyClassService.leave(id)
        val wrappedResponse: BaseResponse<StudyClassLeaveResponse> = BaseResponse("스터디 나가기 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }
}