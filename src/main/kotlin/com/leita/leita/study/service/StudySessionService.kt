package com.leita.leita.study.service

import com.leita.leita.common.exception.CustomException
import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.study.controller.StudySessionMapper
import com.leita.leita.study.domain.*
import com.leita.leita.study.dto.*
import com.leita.leita.judge.repository.JudgeRepository
import com.leita.leita.problem.repository.ProblemRepository
import com.leita.leita.study.repository.AssignmentRecordRepository
import com.leita.leita.study.repository.StudyMemberRepository
import com.leita.leita.study.repository.StudyRepository
import com.leita.leita.study.repository.StudySessionRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StudySessionService(
    private val studyRepository: StudyRepository,
    private val studySessionRepository: StudySessionRepository,
    private val studyMemberRepository: StudyMemberRepository,
    private val assignmentRecordRepository: AssignmentRecordRepository,
    private val judgeRepository: JudgeRepository,
    private val problemRepository: ProblemRepository,
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
        val sessions = studySessionRepository.findByStudyIdOrderByStartDateTimeDesc(studyId, pageable)

        return StudySessionMapper.toStudySessionsResponse(sessions)
    }

    @Transactional(readOnly = true)
    fun getStudySession(sessionId: Long): StudySessionDetailResponse {
        val email = jwtUtils.extractEmail()
        val studySession = getStudySessionEntity(sessionId)
        val study = getStudy(studySession.studyId)
        study.checkMemberByEmail(email)

        val member = study.studyMembers.find { it.user.email == email }
        return StudySessionMapper.toStudySessionDetailResponse(studySession, member?.user?.id)
    }

    @Transactional
    fun createStudySession(studyId: Long, request: StudySessionCreateRequest): StudySessionDetailResponse {
        val email = jwtUtils.extractEmail()
        val study = getStudy(studyId)
        study.checkAdminByEmail(email)

        val studySession = StudySession.create(
            title = request.title,
            description = request.description,
            startDateTime = request.startDateTime,
            endDateTime = request.endDateTime,
            studyId = studyId
        )
        val saved = studySessionRepository.save(studySession)
        val member = study.studyMembers.find { it.user.email == email }
        return StudySessionMapper.toStudySessionDetailResponse(saved, member?.user?.id)
    }

    @Transactional
    fun updateStudySession(sessionId: Long, request: StudySessionUpdateRequest): StudySessionDetailResponse {
        val email = jwtUtils.extractEmail()
        val studySession = getStudySessionEntity(sessionId)
        val study = getStudy(studySession.studyId)
        study.checkAdminByEmail(email)

        studySession.update(
            title = request.title,
            description = request.description,
            startDateTime = request.startDateTime,
            endDateTime = request.endDateTime
        )
        val member = study.studyMembers.find { it.user.email == email }
        return StudySessionMapper.toStudySessionDetailResponse(studySession, member?.user?.id)
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

    @Transactional
    fun updateMemberAttendanceStatus(
        sessionId: Long,
        memberId: Long,
        request: MemberAttendanceUpdateRequest
    ): AttendanceResponse {
        val studySession = getStudySessionEntity(sessionId)
        val study = getStudy(studySession.studyId)
        study.checkAdminByEmail(jwtUtils.extractEmail())

        val member = studyMemberRepository.findById(memberId)
            .orElseThrow { CustomException("Member not found", HttpStatus.NOT_FOUND) }

        if (member.study.id != study.id) {
            throw CustomException("Member does not belong to this study", HttpStatus.BAD_REQUEST)
        }

        val attendance = getLatestAttendance(studySession)
        val record = attendance.records.find { it.user.id == member.user.id }
            ?: throw CustomException("Attendance record not found", HttpStatus.NOT_FOUND)

        record.status = request.status
        return StudySessionMapper.toAttendanceResponse(attendance)
    }

    @Transactional(readOnly = true)
    fun getAssignment(sessionId: Long): AssignmentDetailResponse {
        val email = jwtUtils.extractEmail()
        val studySession = getStudySessionEntity(sessionId)
        val study = getStudy(studySession.studyId)
        study.checkMemberByEmail(email)

        val member = study.studyMembers.find { it.user.email == email }
        val assignment = getAssignmentEntity(studySession)
        return StudySessionMapper.toAssignmentDetailResponse(assignment, member?.user?.id)
    }

    @Transactional
    fun updateMemberAssignmentStatus(
        sessionId: Long,
        memberId: Long,
        request: MemberAssignmentUpdateRequest
    ): AssignmentDetailResponse {
        val studySession = getStudySessionEntity(sessionId)
        val study = getStudy(studySession.studyId)
        study.checkAdminByEmail(jwtUtils.extractEmail())

        val member = studyMemberRepository.findById(memberId)
            .orElseThrow { CustomException("Member not found", HttpStatus.NOT_FOUND) }

        if (member.study.id != study.id) {
            throw CustomException("Member does not belong to this study", HttpStatus.BAD_REQUEST)
        }

        val assignment = getAssignmentEntity(studySession)
        val record = assignment.records.find { it.user.id == member.user.id }
            ?: AssignmentRecord(assignment, member.user, request.status).also { assignment.records.add(it) }

        record.updateStatus(request.status)
        return StudySessionMapper.toAssignmentDetailResponse(assignment, member.user.id)
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
            description = request.description,
            problemIds = request.problemIds,
            startDateTime = request.startDateTime,
            endDateTime = request.endDateTime
        )
        
        syncAssignmentRecords(study, assignment)
        
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
            description = request.description,
            problemIds = request.problemIds,
            startDateTime = request.startDateTime,
            endDateTime = request.endDateTime
        )
        
        syncAssignmentRecords(study, assignment)
        
        return StudySessionMapper.toAssignmentResponse(assignment)
    }

    private fun syncAssignmentRecords(study: Study, assignment: Assignment) {
        val activeMembers = study.getAllActiveMembers()
        val problemIds = assignment.problemIds

        activeMembers.forEach { member ->
            val record = assignment.records.find { it.user.id == member.id }
                ?: AssignmentRecord(assignment, member, AssignmentStatus.INCOMPLETE).also { assignment.records.add(it) }

            if (problemIds.isEmpty()) {
                record.updateStatus(AssignmentStatus.COMPLETED)
            } else {
                val correctJudges = judgeRepository.findByProblemIdInAndUserIdAndResult(problemIds, member.id, com.leita.leita.judge.domain.Result.CORRECT)
                val correctProblemIds = correctJudges.map { it.problemId }.distinct()

                val submitJudges = judgeRepository.findByProblemIdInAndUserIdAndType(problemIds, member.id, com.leita.leita.judge.domain.JudgeType.SUBMIT)
                val submittedProblemIds = submitJudges.map { it.problemId }.distinct()

                val status = when {
                    correctProblemIds.size == problemIds.size -> AssignmentStatus.COMPLETED
                    submittedProblemIds.size == problemIds.size -> AssignmentStatus.PARTIAL
                    else -> AssignmentStatus.INCOMPLETE
                }
                record.updateStatus(status)
            }
        }
        
        // Remove records for users who are no longer active members (optional, but good for cleanup)
        assignment.records.removeIf { record -> activeMembers.none { it.id == record.user.id } }
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
