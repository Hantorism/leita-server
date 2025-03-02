package com.leita.leita.controller

import com.leita.leita.controller.dto.BaseResponse
import com.leita.leita.controller.dto.problem.response.StudiesResponse
import com.leita.leita.controller.dto.study.request.StudyCreateRequest
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
        val wrappedResponse: BaseResponse<StudiesResponse> = BaseResponse("문제 삭제 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @GetMapping("/{id}")
    fun getStudy(@PathVariable id: Long): ResponseEntity<BaseResponse<StudyDetailResponse>> {
        val response = studyService.getStudy(id)
        val wrappedResponse: BaseResponse<StudyDetailResponse> = BaseResponse("문제 삭제 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @PostMapping
    fun createStudy(@RequestBody request: StudyCreateRequest): ResponseEntity<BaseResponse<StudyCreateResponse>> {
        val response = studyService.create(request)
        val wrappedResponse: BaseResponse<StudyCreateResponse> = BaseResponse("문제 삭제 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @GetMapping("/{id}/join")
    fun join(@PathVariable id: Long): ResponseEntity<BaseResponse<StudyJoinResponse>> {
        val response = studyService.join(id)
        val wrappedResponse: BaseResponse<StudyJoinResponse> = BaseResponse("문제 삭제 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @GetMapping("/{id}/approve")
    fun approve(@PathVariable id: Long, @RequestParam email: String): ResponseEntity<BaseResponse<StudyApproveResponse>> {
        val response = studyService.approve(id, email)
        val wrappedResponse: BaseResponse<StudyApproveResponse> = BaseResponse("문제 삭제 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @GetMapping("/{id}/deny")
    fun deny(@PathVariable id: Long, @RequestParam email: String): ResponseEntity<BaseResponse<StudyDenyResponse>> {
        val response = studyService.deny(id, email)
        val wrappedResponse: BaseResponse<StudyDenyResponse> = BaseResponse("문제 삭제 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @GetMapping("/{id}/leave")
    fun approve(@PathVariable id: Long): ResponseEntity<BaseResponse<StudyLeaveResponse>> {
        val response = studyService.leave(id)
        val wrappedResponse: BaseResponse<StudyLeaveResponse> = BaseResponse("문제 삭제 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }
}