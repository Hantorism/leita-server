package com.leita.leita.controller

import com.leita.leita.controller.dto.BaseResponse
import com.leita.leita.controller.dto.study.request.StudyCreateRequest
import com.leita.leita.controller.dto.study.request.StudyUpdateRequest
import com.leita.leita.controller.dto.study.response.StudiesResponse
import com.leita.leita.controller.dto.study.response.StudyCreateResponse
import com.leita.leita.controller.dto.study.response.StudyDetailResponse
import com.leita.leita.service.StudyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/study")
class StudyController(private val studyService: StudyService) {

    @GetMapping
    fun getStudies(
        @RequestParam page: Int = 0, @RequestParam size: Int = 10,
        @RequestParam classId: Long
    ): ResponseEntity<BaseResponse<StudiesResponse>> {
        val response = studyService.getAll(classId, page, size)
        val wrappedResponse: BaseResponse<StudiesResponse> = BaseResponse("스터디 조회 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @GetMapping("/{id}")
    fun getStudy(@PathVariable id: Long): ResponseEntity<BaseResponse<StudyDetailResponse>> {
        val response = studyService.get(id)
        val wrappedResponse: BaseResponse<StudyDetailResponse> = BaseResponse("스터디 조회 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @PutMapping("/{id}")
    fun updateStudy(@PathVariable id: Long, request: StudyUpdateRequest ): ResponseEntity<BaseResponse<Void>> {
        studyService.update(id, request)
        val wrappedResponse: BaseResponse<Void> = BaseResponse("스터디 수정 완료", null)
        return ResponseEntity.ok(wrappedResponse)
    }

    @DeleteMapping("/{id}")
    fun deleteStudy(@PathVariable id: Long): ResponseEntity<BaseResponse<Void>> {
        studyService.delete(id)
        val wrappedResponse: BaseResponse<Void> = BaseResponse("스터디 삭제 완료", null)
        return ResponseEntity.ok(wrappedResponse)
    }

    @PostMapping
    fun createStudy(
        @RequestParam classId: Long,
        @RequestBody request: StudyCreateRequest
    ): ResponseEntity<BaseResponse<StudyCreateResponse>> {
        val response = studyService.create(classId, request)
        val wrappedResponse: BaseResponse<StudyCreateResponse> = BaseResponse("스터디 생성 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @GetMapping("/{id}/attend")
    fun attend(@PathVariable id: Long): ResponseEntity<BaseResponse<Void>> {
        studyService.attend(id)
        val wrappedResponse: BaseResponse<Void> = BaseResponse("스터디 참가 신청 완료", null)
        return ResponseEntity.ok(wrappedResponse)
    }
}