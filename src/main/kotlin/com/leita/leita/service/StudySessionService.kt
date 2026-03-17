package com.leita.leita.service

import com.leita.leita.common.exception.CustomException
import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.controller.study.StudySessionMapper
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
import com.leita.leita.domain.study.AttendanceStatus
import com.leita.leita.domain.study.Study
import com.leita.leita.domain.study.StudySession
import com.leita.leita.repository.StudyRepository
import com.leita.leita.repository.StudySessionRepository
import org.springframework.data.domain.PageRequest
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
    fun getStudySessions(studyId: Long, page: Int, size: Int): StudySessionsResponse {
        val study = getStudy(studyId)
        study.checkMemberByEmail(jwtUtils.extractEmail())

        val sessions = studySessionRepository.findByStudyIdOrderByStartDateTimeAsc(
            studyId,
            PageRequest.of(page, size)
        )
        return StudySessionMapper.toStudySessionsResponse(sessions)
    }

    @Transactional(readOnly = true)
    fun getStudySession(studyId: Long, sessionId: Long): StudySessionDetailResponse {
        val study = getStudy(studyId)
        study.checkMemberByEmail(jwtUtils.extractEmail())

        val studySession = getStudySessionEntity(studyId, sessionId)
        return StudySessionMapper.toStudySessionDetailResponse(studySession)
    }

    @Transactional
    fun createStudySession(studyId: Long, request: StudySessionCreateRequest): StudySessionDetailResponse {
        val study = getStudy(studyId)
        study.checkAdminByEmail(jwtUtils.extractEmail())

        val studySession = StudySession.create(
            startDateTime = request.startDateTime,
            endDateTime = request.endDateTime,
            studyId = studyId
        )
        val saved = studySessionRepository.save(studySession)
        return StudySessionMapper.toStudySessionDetailResponse(saved)
    }

    @Transactional
    fun updateStudySession(studyId: Long, sessionId: Long, request: StudySessionUpdateRequest): StudySessionDetailResponse {
        val study = getStudy(studyId)
        study.checkAdminByEmail(jwtUtils.extractEmail())

        val studySession = getStudySessionEntity(studyId, sessionId)
        studySession.update(request.startDateTime, request.endDateTime)
        return StudySessionMapper.toStudySessionDetailResponse(studySession)
    }

    @Transactional
    fun deleteStudySession(studyId: Long, sessionId: Long) {
        val study = getStudy(studyId)
        study.checkAdminByEmail(jwtUtils.extractEmail())

        val studySession = getStudySessionEntity(studyId, sessionId)
        studySessionRepository.delete(studySession)
    }

    @Transactional(readOnly = true)
    fun getAttendance(studyId: Long, sessionId: Long): AttendanceResponse {
        val study = getStudy(studyId)
        study.checkMemberByEmail(jwtUtils.extractEmail())

        val attendance = getLatestAttendance(studyId, sessionId)
        return StudySessionMapper.toAttendanceResponse(attendance)
    }

    @Transactional
    fun openAttendance(
        studyId: Long,
        sessionId: Long,
        request: AttendanceOpenRequest
    ): AttendanceResponse {
        val study = getStudy(studyId)
        study.checkAdminByEmail(jwtUtils.extractEmail())
        validateAttendanceEnabled(study)

        val studySession = getStudySessionEntity(studyId, sessionId)
        val attendance = studySession.openAttendance(
            openTime = request.openTime ?: studySession.startDateTime,
            closeTime = request.closeTime ?: studySession.endDateTime,
            lateThresholdMinutes = request.lateThresholdMinutes ?: 10
        )
        study.getAllActiveMembers().forEach(attendance::registerMember)

        return StudySessionMapper.toAttendanceResponse(attendance)
    }

    @Transactional
    fun closeAttendance(
        studyId: Long,
        sessionId: Long,
        request: AttendanceCloseRequest?
    ): AttendanceResponse {
        val study = getStudy(studyId)
        study.checkAdminByEmail(jwtUtils.extractEmail())
        validateAttendanceEnabled(study)

        val attendance = getOpenAttendance(studyId, sessionId)
        attendance.close(request?.closeTime ?: LocalDateTime.now())
        return StudySessionMapper.toAttendanceResponse(attendance)
    }

    @Transactional
    fun attend(studyId: Long, sessionId: Long): AttendanceResponse {
        val user = jwtUtils.extractUser()
        val study = getStudy(studyId)
        study.checkMemberByEmail(user.email)
        validateAttendanceEnabled(study)

        val attendance = getOpenAttendance(studyId, sessionId)
        attendance.attend(user)
        return StudySessionMapper.toAttendanceResponse(attendance)
    }

    @Transactional(readOnly = true)
    fun getAssignment(studyId: Long, sessionId: Long): AssignmentDetailResponse {
        val study = getStudy(studyId)
        study.checkMemberByEmail(jwtUtils.extractEmail())
        validateAssignmentEnabled(study)

        val assignment = getAssignmentEntity(studyId, sessionId)
        return StudySessionMapper.toAssignmentDetailResponse(assignment)
    }

    @Transactional
    fun createAssignment(
        studyId: Long,
        sessionId: Long,
        request: AssignmentCreateRequest
    ): AssignmentResponse {
        val study = getStudy(studyId)
        study.checkAdminByEmail(jwtUtils.extractEmail())
        validateAssignmentEnabled(study)

        val studySession = getStudySessionEntity(studyId, sessionId)
        val assignment = studySession.createAssignment(
            title = request.title,
            description = request.description,
            problemIds = request.problemIds
        )
        return StudySessionMapper.toAssignmentResponse(assignment)
    }

    @Transactional
    fun updateAssignment(
        studyId: Long,
        sessionId: Long,
        request: AssignmentUpdateRequest
    ): AssignmentResponse {
        val study = getStudy(studyId)
        study.checkAdminByEmail(jwtUtils.extractEmail())
        validateAssignmentEnabled(study)

        val assignment = getAssignmentEntity(studyId, sessionId)
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

    private fun getStudySessionEntity(studyId: Long, sessionId: Long): StudySession {
        return studySessionRepository.findDetailByIdAndStudyId(sessionId, studyId)
            ?: throw CustomException("Study session not found", HttpStatus.NOT_FOUND)
    }

    private fun getLatestAttendance(studyId: Long, sessionId: Long) =
        getStudySessionEntity(studyId, sessionId).attendances
            .maxByOrNull { it.openTime }
            ?: throw CustomException("Attendance not found", HttpStatus.NOT_FOUND)

    private fun getOpenAttendance(studyId: Long, sessionId: Long) =
        getStudySessionEntity(studyId, sessionId).attendances
            .lastOrNull { it.status == AttendanceStatus.OPEN }
            ?: throw CustomException("Open attendance not found", HttpStatus.NOT_FOUND)

    private fun getAssignmentEntity(studyId: Long, sessionId: Long) =
        getStudySessionEntity(studyId, sessionId).getAssignment()
            ?: throw CustomException("Assignment not found", HttpStatus.NOT_FOUND)

    private fun validateAttendanceEnabled(study: Study) {
        if (!study.attendanceRequired) {
            throw CustomException("이 스터디는 출석을 사용하지 않습니다.", HttpStatus.BAD_REQUEST)
        }
    }

    private fun validateAssignmentEnabled(study: Study) {
        if (!study.assignmentRequired) {
            throw CustomException("이 스터디는 과제를 사용하지 않습니다.", HttpStatus.BAD_REQUEST)
        }
    }
}
