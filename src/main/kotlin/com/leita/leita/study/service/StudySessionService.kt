package com.leita.leita.study.service

import com.leita.leita.common.exception.CustomException
import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.study.controller.StudySessionMapper
import com.leita.leita.study.dto.AssignmentCreateRequest
import com.leita.leita.study.dto.AssignmentUpdateRequest
import com.leita.leita.study.dto.AttendanceOpenRequest
import com.leita.leita.study.dto.AttendanceUpdateRequest
import com.leita.leita.study.dto.AttendanceCloseRequest
import com.leita.leita.study.dto.StudySessionCreateRequest
import com.leita.leita.study.dto.StudySessionUpdateRequest
import com.leita.leita.study.dto.AssignmentDetailResponse
import com.leita.leita.study.dto.AssignmentResponse
import com.leita.leita.study.dto.AttendanceResponse
import com.leita.leita.study.dto.StudySessionDetailResponse
import com.leita.leita.study.dto.StudySessionsResponse
import com.leita.leita.study.domain.AttendanceStatus
import com.leita.leita.study.domain.Study
import com.leita.leita.study.domain.StudySession
import com.leita.leita.study.repository.StudyRepository
import com.leita.leita.study.repository.StudySessionRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class StudySessionService(
    private val studyRepository: StudyRepository,
    private val studySessionRepository: StudySessionRepository,
    private val jwtUtils: JwtUtils
) {

    @Transactional(readOnly = true)
    fun getStudySessions(studyId: Long, page: Int?, size: Int?): StudySessionsResponse {
        val study = getStudy(studyId)
        study.checkMemberByEmail(jwtUtils.extractEmail())

        var pageable: Pageable
        if (page != null && size != null) {
            pageable = PageRequest.of(page, size)
        } else {
            pageable = Pageable.unpaged()
        }
        val sessions = studySessionRepository.findByStudyIdOrderByStartDateTimeAsc(studyId, pageable)

        return StudySessionMapper.toStudySessionsResponse(sessions)
    }

    @Transactional(readOnly = true)
    fun getStudySession(sessionId: Long): StudySessionDetailResponse {
        val studySession = getStudySessionEntity(sessionId)
        val study = getStudy(studySession.studyId)
        study.checkMemberByEmail(jwtUtils.extractEmail())

        return StudySessionMapper.toStudySessionDetailResponse(studySession)
    }

    @Transactional
    fun createStudySession(studyId: Long, request: StudySessionCreateRequest): StudySessionDetailResponse {
        val study = getStudy(studyId)
        study.checkAdminByEmail(jwtUtils.extractEmail())

        val studySession = StudySession.create(
            title = request.title,
            description = request.description,
            startDateTime = request.startDateTime,
            endDateTime = request.endDateTime,
            studyId = studyId
        )
        val saved = studySessionRepository.save(studySession)
        return StudySessionMapper.toStudySessionDetailResponse(saved)
    }

    @Transactional
    fun updateStudySession(sessionId: Long, request: StudySessionUpdateRequest): StudySessionDetailResponse {
        val studySession = getStudySessionEntity(sessionId)
        val study = getStudy(studySession.studyId)
        study.checkAdminByEmail(jwtUtils.extractEmail())

        studySession.update(
            title = request.title,
            description = request.description,
            startDateTime = request.startDateTime,
            endDateTime = request.endDateTime
        )
        return StudySessionMapper.toStudySessionDetailResponse(studySession)
    }

    @Transactional
    fun deleteStudySession(sessionId: Long) {
        val studySession = getStudySessionEntity(sessionId)
        val study = getStudy(studySession.studyId)
        study.checkAdminByEmail(jwtUtils.extractEmail())

        studySessionRepository.delete(studySession)
    }

    @Transactional(readOnly = true)
    fun getAttendance(sessionId: Long): AttendanceResponse {
        val studySession = getStudySessionEntity(sessionId)
        val study = getStudy(studySession.studyId)
        study.checkMemberByEmail(jwtUtils.extractEmail())

        val attendance = getLatestAttendance(studySession)
        return StudySessionMapper.toAttendanceResponse(attendance)
    }

    @Transactional
    fun openAttendance(
        sessionId: Long, request: AttendanceOpenRequest
    ): AttendanceResponse {
        val studySession = getStudySessionEntity(sessionId)
        val study = getStudy(studySession.studyId)
        study.checkAdminByEmail(jwtUtils.extractEmail())

        val attendance = studySession.openAttendance(
            openTime = request.openTime,
            closeTime = request.closeTime,
            lateThresholdMinutes = request.lateThresholdMinutes
        )
        study.getAllActiveMembers().forEach(attendance::registerMember)

        return StudySessionMapper.toAttendanceResponse(attendance)
    }

    @Transactional
    fun updateAttendance(
        sessionId: Long,
        request: AttendanceUpdateRequest
    ): AttendanceResponse {
        val studySession = getStudySessionEntity(sessionId)
        val study = getStudy(studySession.studyId)
        study.checkAdminByEmail(jwtUtils.extractEmail())

        val attendance = getLatestAttendance(studySession)
        attendance.update(
            closeTime = request.closeTime,
            lateThresholdMinutes = request.lateThresholdMinutes,
            status = request.status
        )
        return StudySessionMapper.toAttendanceResponse(attendance)
    }

    @Transactional
    fun attend(sessionId: Long): AttendanceResponse {
        val user = jwtUtils.extractUser()
        val studySession = getStudySessionEntity(sessionId)
        val study = getStudy(studySession.studyId)
        study.checkMemberByEmail(user.email)

        val attendance = getOpenAttendance(studySession)
        attendance.attend(user)
        return StudySessionMapper.toAttendanceResponse(attendance)
    }

    @Transactional(readOnly = true)
    fun getAssignment(sessionId: Long): AssignmentDetailResponse {
        val studySession = getStudySessionEntity(sessionId)
        val study = getStudy(studySession.studyId)
        study.checkMemberByEmail(jwtUtils.extractEmail())

        val assignment = getAssignmentEntity(studySession)
        return StudySessionMapper.toAssignmentDetailResponse(assignment)
    }

    @Transactional
    fun createAssignment(
        sessionId: Long,
        request: AssignmentCreateRequest
    ): AssignmentResponse {
        val studySession = getStudySessionEntity(sessionId)
        val study = getStudy(studySession.studyId)
        study.checkAdminByEmail(jwtUtils.extractEmail())

        val assignment = studySession.createAssignment(
            title = request.title,
            description = request.description,
            problemIds = request.problemIds
        )
        return StudySessionMapper.toAssignmentResponse(assignment)
    }

    @Transactional
    fun updateAssignment(
        sessionId: Long,
        request: AssignmentUpdateRequest
    ): AssignmentResponse {
        val studySession = getStudySessionEntity(sessionId)
        val study = getStudy(studySession.studyId)
        study.checkAdminByEmail(jwtUtils.extractEmail())

        val assignment = getAssignmentEntity(studySession)
        assignment.update(
            title = request.title,
            description = request.description,
            problemIds = request.problemIds
        )
        return StudySessionMapper.toAssignmentResponse(assignment)
    }

    private fun getStudy(studyId: Long): Study {
        return studyRepository.findDetailById(studyId)
            ?: throw CustomException("Study not found", HttpStatus.NOT_FOUND)
    }

    private fun getStudySessionEntity(sessionId: Long): StudySession {
        return studySessionRepository.findDetailById(sessionId)
            ?: throw CustomException("Study session not found", HttpStatus.NOT_FOUND)
    }

    private fun getLatestAttendance(studySession: StudySession) =
        studySession.attendances
            .maxByOrNull { it.openTime }
            ?: throw CustomException("Attendance not found", HttpStatus.NOT_FOUND)

    private fun getOpenAttendance(studySession: StudySession) =
        studySession.attendances
            .lastOrNull { it.status == AttendanceStatus.OPEN }
            ?: throw CustomException("Open attendance not found", HttpStatus.NOT_FOUND)

    private fun getAssignmentEntity(studySession: StudySession) =
        studySession.getAssignment()
            ?: throw CustomException("Assignment not found", HttpStatus.NOT_FOUND)

}
