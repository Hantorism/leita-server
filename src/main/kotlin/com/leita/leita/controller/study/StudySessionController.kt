package com.leita.leita.controller.study

import com.leita.leita.controller.dto.BaseResponse
import com.leita.leita.controller.study.request.AssignmentCreateRequest
import com.leita.leita.controller.study.request.AssignmentUpdateRequest
import com.leita.leita.controller.study.request.AttendanceCloseRequest
import com.leita.leita.controller.study.request.AttendanceOpenRequest
import com.leita.leita.controller.study.request.StudySessionCreateRequest
import com.leita.leita.controller.study.request.StudySessionUpdateRequest
import com.leita.leita.controller.study.response.AssignmentDetailResponse
import com.leita.leita.controller.study.response.AssignmentResponse
import com.leita.leita.controller.study.response.AttendanceResponse
import com.leita.leita.controller.study.response.StudySessionDetailResponse
import com.leita.leita.controller.study.response.StudySessionsResponse
import com.leita.leita.service.StudySessionService
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
        @PathVariable studyId: Long,
        @RequestParam page: Int = 0,
        @RequestParam size: Int = 10
    ): ResponseEntity<BaseResponse<StudySessionsResponse>> {
        val response = studySessionService.getStudySessions(studyId, page, size)
        return ResponseEntity.ok(BaseResponse("스터디 세션 목록 조회 완료", response))
    }

    @GetMapping("/{sessionId}")
    fun getStudySession(
        @PathVariable studyId: Long,
        @PathVariable sessionId: Long
    ): ResponseEntity<BaseResponse<StudySessionDetailResponse>> {
        val response = studySessionService.getStudySession(studyId, sessionId)
        return ResponseEntity.ok(BaseResponse("스터디 세션 조회 완료", response))
    }

    @PostMapping
    fun createStudySession(
        @PathVariable studyId: Long,
        @RequestBody request: StudySessionCreateRequest
    ): ResponseEntity<BaseResponse<StudySessionDetailResponse>> {
        val response = studySessionService.createStudySession(studyId, request)
        return ResponseEntity.ok(BaseResponse("스터디 세션 생성 완료", response))
    }

    @PutMapping("/{sessionId}")
    fun updateStudySession(
        @PathVariable studyId: Long,
        @PathVariable sessionId: Long,
        @RequestBody request: StudySessionUpdateRequest
    ): ResponseEntity<BaseResponse<StudySessionDetailResponse>> {
        val response = studySessionService.updateStudySession(studyId, sessionId, request)
        return ResponseEntity.ok(BaseResponse("스터디 세션 수정 완료", response))
    }

    @DeleteMapping("/{sessionId}")
    fun deleteStudySession(
        @PathVariable studyId: Long,
        @PathVariable sessionId: Long
    ): ResponseEntity<BaseResponse<Void>> {
        studySessionService.deleteStudySession(studyId, sessionId)
        return ResponseEntity.ok(BaseResponse("스터디 세션 삭제 완료", null))
    }

    @GetMapping("/{sessionId}/attendance")
    fun getAttendance(
        @PathVariable studyId: Long,
        @PathVariable sessionId: Long
    ): ResponseEntity<BaseResponse<AttendanceResponse>> {
        val response = studySessionService.getAttendance(studyId, sessionId)
        return ResponseEntity.ok(BaseResponse("출석 조회 완료", response))
    }

    @PostMapping("/{sessionId}/attendance/open")
    fun openAttendance(
        @PathVariable studyId: Long,
        @PathVariable sessionId: Long,
        @RequestBody request: AttendanceOpenRequest
    ): ResponseEntity<BaseResponse<AttendanceResponse>> {
        val response = studySessionService.openAttendance(studyId, sessionId, request)
        return ResponseEntity.ok(BaseResponse("출석 오픈 완료", response))
    }

    @PostMapping("/{sessionId}/attendance/attend")
    fun attend(
        @PathVariable studyId: Long,
        @PathVariable sessionId: Long
    ): ResponseEntity<BaseResponse<AttendanceResponse>> {
        val response = studySessionService.attend(studyId, sessionId)
        return ResponseEntity.ok(BaseResponse("출석 완료", response))
    }

    @PostMapping("/{sessionId}/attendance/close")
    fun closeAttendance(
        @PathVariable studyId: Long,
        @PathVariable sessionId: Long,
        @RequestBody(required = false) request: AttendanceCloseRequest?
    ): ResponseEntity<BaseResponse<AttendanceResponse>> {
        val response = studySessionService.closeAttendance(studyId, sessionId, request)
        return ResponseEntity.ok(BaseResponse("출석 종료 완료", response))
    }

    @GetMapping("/{sessionId}/assignment")
    fun getAssignment(
        @PathVariable studyId: Long,
        @PathVariable sessionId: Long
    ): ResponseEntity<BaseResponse<AssignmentDetailResponse>> {
        val response = studySessionService.getAssignment(studyId, sessionId)
        return ResponseEntity.ok(BaseResponse("과제 조회 완료", response))
    }

    @PostMapping("/{sessionId}/assignment")
    fun createAssignment(
        @PathVariable studyId: Long,
        @PathVariable sessionId: Long,
        @RequestBody request: AssignmentCreateRequest
    ): ResponseEntity<BaseResponse<AssignmentResponse>> {
        val response = studySessionService.createAssignment(studyId, sessionId, request)
        return ResponseEntity.ok(BaseResponse("과제 생성 완료", response))
    }

    @PutMapping("/{sessionId}/assignment")
    fun updateAssignment(
        @PathVariable studyId: Long,
        @PathVariable sessionId: Long,
        @RequestBody request: AssignmentUpdateRequest
    ): ResponseEntity<BaseResponse<AssignmentResponse>> {
        val response = studySessionService.updateAssignment(studyId, sessionId, request)
        return ResponseEntity.ok(BaseResponse("과제 수정 완료", response))
    }
}
