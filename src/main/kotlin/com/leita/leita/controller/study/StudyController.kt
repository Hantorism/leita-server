package com.leita.leita.controller.study

import com.leita.leita.controller.dto.BaseResponse
import com.leita.leita.controller.study.request.StudyCreateRequest
import com.leita.leita.controller.study.request.StudyMemberRequest
import com.leita.leita.controller.study.request.StudyUpdateRequest
import com.leita.leita.controller.study.response.StudiesResponse
import com.leita.leita.controller.study.response.StudyCreateResponse
import com.leita.leita.controller.study.response.StudyDetailResponse
import com.leita.leita.controller.study.response.StudyMemberResponse
import com.leita.leita.service.StudyService
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/study")
class StudyController(
    private val studyService: StudyService
) {

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

    @PutMapping("/{id}")
    fun updateStudy(@PathVariable id: Long, @RequestBody request: StudyUpdateRequest): ResponseEntity<BaseResponse<StudyDetailResponse>> {
        val response = studyService.updateStudy(id, request)
        val wrappedResponse: BaseResponse<StudyDetailResponse> = BaseResponse("스터디 수정 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @DeleteMapping("/{id}")
    fun deleteStudy(@PathVariable id: Long): ResponseEntity<BaseResponse<Void>> {
        studyService.deleteStudy(id)
        val wrappedResponse: BaseResponse<Void> = BaseResponse("스터디 삭제 완료", null)
        return ResponseEntity.ok(wrappedResponse)
    }

    @PostMapping
    fun createStudy(@RequestBody request: StudyCreateRequest): ResponseEntity<BaseResponse<StudyCreateResponse>> {
        val response = studyService.create(request)
        val wrappedResponse: BaseResponse<StudyCreateResponse> = BaseResponse("스터디 생성 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @PostMapping("/{id}/join")
    fun joinStudy(@PathVariable id: Long): ResponseEntity<BaseResponse<Void>> {
        studyService.join(id)
        val wrappedResponse: BaseResponse<Void> = BaseResponse("스터디 참가 신청 완료", null)
        return ResponseEntity.ok(wrappedResponse)
    }

    @PostMapping("/{id}/approve")
    fun approveStudyMember(
        @PathVariable id: Long,
        @RequestBody request: StudyMemberRequest
    ): ResponseEntity<BaseResponse<Void>> {
        studyService.approve(id, request)
        val wrappedResponse: BaseResponse<Void> = BaseResponse("스터디 참가 승인 완료", null)
        return ResponseEntity.ok(wrappedResponse)
    }

    @PostMapping("/{id}/deny")
    fun denyStudyMember(
        @PathVariable id: Long,
        @RequestBody request: StudyMemberRequest
    ): ResponseEntity<BaseResponse<Void>> {
        studyService.deny(id, request)
        val wrappedResponse: BaseResponse<Void> = BaseResponse("스터디 참가 거절 완료", null)
        return ResponseEntity.ok(wrappedResponse)
    }

    @PostMapping("/{id}/leave")
    fun leaveStudy(@PathVariable id: Long): ResponseEntity<BaseResponse<Void>> {
        studyService.leave(id)
        val wrappedResponse: BaseResponse<Void> = BaseResponse("스터디 탈퇴 완료", null)
        return ResponseEntity.ok(wrappedResponse)
    }

    @GetMapping("/{id}/pendings")
    fun getPendingMembers(
        @PathVariable id: Long,
        @RequestParam page: Int = 0,
        @RequestParam size: Int = 10
    ): ResponseEntity<BaseResponse<Page<StudyMemberResponse>>> {
        val response = studyService.getPendingMembersPage(id, page, size)
        val wrappedResponse = BaseResponse("대기 멤버 조회 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }
}