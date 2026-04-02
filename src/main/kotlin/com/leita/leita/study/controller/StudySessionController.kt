package com.leita.leita.study.controller

import com.leita.leita.common.dto.BaseResponse
import com.leita.leita.study.dto.AssignmentCreateRequest
import com.leita.leita.study.dto.AssignmentUpdateRequest
import com.leita.leita.study.dto.AttendanceCloseRequest
import com.leita.leita.study.dto.AttendanceOpenRequest
import com.leita.leita.study.dto.StudySessionCreateRequest
import com.leita.leita.study.dto.StudySessionUpdateRequest
import com.leita.leita.study.dto.AssignmentDetailResponse
import com.leita.leita.study.dto.AssignmentResponse
import com.leita.leita.study.dto.AttendanceResponse
import com.leita.leita.study.dto.StudySessionDetailResponse
import com.leita.leita.study.dto.StudySessionsResponse
import com.leita.leita.study.service.StudySessionService
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
@RequestMapping("/study-session")
class StudySessionController(
    private val studySessionService: StudySessionService
) {

    @GetMapping
    fun getStudySessions(
        @RequestParam(required = true) studyId: Long,
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?
    ): ResponseEntity<BaseResponse<StudySessionsResponse>> {
        val response = studySessionService.getStudySessions(studyId, page, size)
        return ResponseEntity.ok(BaseResponse("스터디 세션 목록 조회 완료", response))
    }

    @PostMapping
    fun createStudySession(
        @RequestBody request: StudySessionCreateRequest
    ): ResponseEntity<BaseResponse<StudySessionDetailResponse>> {
        val response = studySessionService.createStudySession(request.studyId, request)
        return ResponseEntity.ok(BaseResponse("스터디 세션 생성 완료", response))
    }

    @GetMapping("/{studySessionId}")
    fun getStudySession(
        @PathVariable studySessionId: Long
    ): ResponseEntity<BaseResponse<StudySessionDetailResponse>> {
        val response = studySessionService.getStudySession(studySessionId)
        return ResponseEntity.ok(BaseResponse("스터디 세션 조회 완료", response))
    }

    @PutMapping("/{studySessionId}")
    fun updateStudySession(
        @PathVariable studySessionId: Long,
        @RequestBody request: StudySessionUpdateRequest
    ): ResponseEntity<BaseResponse<StudySessionDetailResponse>> {
        val response = studySessionService.updateStudySession(studySessionId, request)
        return ResponseEntity.ok(BaseResponse("스터디 세션 수정 완료", response))
    }

    @DeleteMapping("/{studySessionId}")
    fun deleteStudySession(
        @PathVariable studySessionId: Long
    ): ResponseEntity<BaseResponse<Void>> {
        studySessionService.deleteStudySession(studySessionId)
        return ResponseEntity.ok(BaseResponse("스터디 세션 삭제 완료", null))
    }

    @GetMapping("/{studySessionId}/attendance")
    fun getAttendance(
        @PathVariable studySessionId: Long
    ): ResponseEntity<BaseResponse<AttendanceResponse>> {
        val response = studySessionService.getAttendance(studySessionId)
        return ResponseEntity.ok(BaseResponse("출석 조회 완료", response))
    }

    @PostMapping("/{studySessionId}/attendance/open")
    fun openAttendance(
        @PathVariable studySessionId: Long,
        @RequestBody request: AttendanceOpenRequest
    ): ResponseEntity<BaseResponse<AttendanceResponse>> {
        val response = studySessionService.openAttendance(studySessionId, request)
        return ResponseEntity.ok(BaseResponse("출석 오픈 완료", response))
    }

    @PostMapping("/{studySessionId}/attendance/attend")
    fun attend(
        @PathVariable studySessionId: Long
    ): ResponseEntity<BaseResponse<AttendanceResponse>> {
        val response = studySessionService.attend(studySessionId)
        return ResponseEntity.ok(BaseResponse("출석 완료", response))
    }

    @PostMapping("/{studySessionId}/attendance/close")
    fun closeAttendance(
        @PathVariable studySessionId: Long,
        @RequestBody(required = false) request: AttendanceCloseRequest?
    ): ResponseEntity<BaseResponse<AttendanceResponse>> {
        val response = studySessionService.closeAttendance(studySessionId, request)
        return ResponseEntity.ok(BaseResponse("출석 종료 완료", response))
    }

    @GetMapping("/{studySessionId}/assignment")
    fun getAssignment(
        @PathVariable studySessionId: Long
    ): ResponseEntity<BaseResponse<AssignmentDetailResponse>> {
        val response = studySessionService.getAssignment(studySessionId)
        return ResponseEntity.ok(BaseResponse("과제 조회 완료", response))
    }

    @PostMapping("/{studySessionId}/assignment")
    fun createAssignment(
        @PathVariable studySessionId: Long,
        @RequestBody request: AssignmentCreateRequest
    ): ResponseEntity<BaseResponse<AssignmentResponse>> {
        val response = studySessionService.createAssignment(studySessionId, request)
        return ResponseEntity.ok(BaseResponse("과제 생성 완료", response))
    }

    @PutMapping("/{studySessionId}/assignment")
    fun updateAssignment(
        @PathVariable studySessionId: Long,
        @RequestBody request: AssignmentUpdateRequest
    ): ResponseEntity<BaseResponse<AssignmentResponse>> {
        val response = studySessionService.updateAssignment(studySessionId, request)
        return ResponseEntity.ok(BaseResponse("과제 수정 완료", response))
    }
}
