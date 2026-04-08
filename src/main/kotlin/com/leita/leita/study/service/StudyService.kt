package com.leita.leita.study.service

import com.leita.leita.common.exception.CustomException
import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.study.controller.StudyMapper
import com.leita.leita.study.dto.*
import com.leita.leita.study.domain.Study
import com.leita.leita.study.domain.StudyMemberRole
import com.leita.leita.user.domain.User
import com.leita.leita.study.repository.StudyMemberRepository
import com.leita.leita.study.repository.StudyRepository
import com.leita.leita.study.repository.StudySessionRepository
import com.leita.leita.judge.repository.JudgeRepository
import com.leita.leita.judge.domain.Result
import com.leita.leita.user.repository.UserRepository
import com.leita.leita.util.mail.MailType
import com.leita.leita.util.mail.MailUtil
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StudyService(
    private val studyRepository: StudyRepository,
    private val studyMemberRepository: StudyMemberRepository,
    private val studySessionRepository: StudySessionRepository,
    private val judgeRepository: JudgeRepository,
    private val userRepository: UserRepository,
    private val jwtUtils: JwtUtils,
    private val mailUtil: MailUtil,
) {

    @Transactional(readOnly = true)
    fun getStudies(page: Int, size: Int): StudiesResponse {
        val pageable: Pageable = PageRequest.of(page, size)
        val studies = studyRepository.findAll(pageable)
        return StudyMapper.toStudiesResponse(studies)
    }

    @Transactional(readOnly = true)
    fun getStudy(id: Long): StudyDetailResponse {
        val study = findStudy(id)
        return StudyMapper.toStudyDetailResponse(study)
    }

    @Transactional
    fun updateStudy(id: Long, request: StudyUpdateRequest): StudyDetailResponse {
        val adminEmail = jwtUtils.extractEmail()
        val study = findStudy(id)
        study.checkAdminByEmail(adminEmail)

        study.update(
            title = request.title,
            description = request.description,
            requirement = request.requirement,
            startDate = request.startDate,
            endDate = request.endDate,
        )
        studyRepository.save(study)
        return StudyMapper.toStudyDetailResponse(study)
    }

    @Transactional
    fun deleteStudy(id: Long) {
        val adminEmail = jwtUtils.extractEmail()
        val study = findStudy(id)
        study.checkAdminByEmail(adminEmail)

        studyRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    fun getStudyMembers(id: Long, role: StudyMemberRole): List<User> {
        val study = findStudy(id)

        return when (role) {
            StudyMemberRole.ADMIN -> study.getAdminUsers()
            StudyMemberRole.MEMBER -> study.getRegularUsers()
            StudyMemberRole.PENDING -> study.getPendingUsers()
        }
    }

    @Transactional
    fun changeRole(id: Long, request: StudyRoleChangeRequest) {
        val adminEmail = jwtUtils.extractEmail()
        val study = findStudy(id)
        study.checkAdminByEmail(adminEmail)

        val user = userRepository.findByEmail(request.email)
            ?: throw CustomException("User not found", HttpStatus.UNAUTHORIZED)

        study.changeRole(user, request.newRole)
        studyRepository.save(study)
    }

    @Transactional
    fun create(request: StudyCreateRequest): StudyCreateResponse {
        val admin = jwtUtils.extractUser()
        val study = Study.create(
            title = request.title,
            description = request.description,
            requirement = request.requirement,
            startDate = request.startDate,
            endDate = request.endDate,
            admin = admin,
        )
        studyRepository.save(study)

        return StudyMapper.toStudyCreateResponse(study)
    }

    @Transactional
    fun join(id: Long) {
        val user = jwtUtils.extractUser()
        val study = findStudy(id)

        study.join(user)
        studyRepository.save(study)
        mailUtil.sendAll(MailType.STUDY_MEMBER_JOIN, study.getAdminUsers().map { it.email })
    }

    @Transactional
    fun approve(id: Long, request: StudyMemberRequest) {
        val adminEmail = jwtUtils.extractEmail()
        val study = findStudy(id)

        study.checkAdminByEmail(adminEmail)
        val user = userRepository.findByEmail(request.email)
            ?: throw CustomException("User not found", HttpStatus.NOT_FOUND)
        study.approve(user)
        studyRepository.save(study)
    }

    @Transactional
    fun deny(id: Long, request: StudyMemberRequest) {
        val adminEmail = jwtUtils.extractEmail()
        val study = findStudy(id)

        study.checkAdminByEmail(adminEmail)
        val user = userRepository.findByEmail(request.email)
            ?: throw CustomException("User not found", HttpStatus.UNAUTHORIZED)
        study.deny(user)
        studyRepository.save(study)
    }

    @Transactional
    fun leave(id: Long) {
        val memberEmail = jwtUtils.extractEmail()
        val study = findStudy(id)

        study.checkMemberByEmail(memberEmail)
        val user = userRepository.findByEmail(memberEmail)
            ?: throw CustomException("User not found", HttpStatus.UNAUTHORIZED)
        study.leave(user)
        studyRepository.save(study)
    }

    @Transactional(readOnly = true)
    fun getPendingMembersPage(studyId: Long, page: Int, size: Int): Page<StudyMemberResponse> {
        val pageable = PageRequest.of(page, size)
        val pendingPage = studyMemberRepository.findByStudyIdAndRoleOrderByJoinedAtDesc(
            studyId = studyId,
            role = StudyMemberRole.PENDING,
            pageable = pageable
        )

        return pendingPage.map {
            StudyMemberResponse(
                userId = it.user.id,
                name = it.user.name,
                email = it.user.email,
                role = it.role,
                joinedAt = it.joinedAt,
                approvedAt = it.approvedAt
            )
        }
    }

    @Transactional(readOnly = true)
    fun getMemberStatus(studyId: Long, studySessionId: Long?, memberId: Long?): List<StudyMemberStatusResponse> {
        val study = findStudy(studyId)
        study.checkMemberByEmail(jwtUtils.extractEmail())

        val members = getTargetMembers(study, memberId)
        val sessions = getTargetSessions(studyId, studySessionId)

        return members.map { member ->
            val sessionStatuses = sessions.map { session ->
                val attendance = session.attendances.maxByOrNull { it.openTime }
                val record = attendance?.records?.find { it.user.id == member.id }
                
                val assignment = session.getAssignment()
                val assignmentRecord = assignment?.records?.find { it.user.id == member.id }

                SessionStatus(
                    sessionId = session.id,
                    sessionTitle = session.title,
                    attendanceStatus = record?.status?.name,
                    assignmentStatus = assignmentRecord?.status?.name
                )
            }
            StudyMemberStatusResponse(
                user = toUserBriefResponse(member),
                sessions = sessionStatuses
            )
        }
    }

    @Transactional(readOnly = true)
    fun getMemberAttendance(studyId: Long, studySessionId: Long?, memberId: Long?): List<StudyMemberAttendanceResponse> {
        val study = findStudy(studyId)
        study.checkMemberByEmail(jwtUtils.extractEmail())

        val members = getTargetMembers(study, memberId)
        val sessions = getTargetSessions(studyId, studySessionId)

        return members.map { member ->
            val attendanceDetails = sessions.map { session ->
                val attendance = session.attendances.maxByOrNull { it.openTime }
                val record = attendance?.records?.find { it.user.id == member.id }
                
                AttendanceDetail(
                    sessionId = session.id,
                    sessionTitle = session.title,
                    status = record?.status?.name,
                    attendedAt = record?.attendedAt
                )
            }
            StudyMemberAttendanceResponse(
                user = toUserBriefResponse(member),
                attendances = attendanceDetails
            )
        }
    }

    @Transactional(readOnly = true)
    fun getMemberAssignment(studyId: Long, studySessionId: Long?, memberId: Long?): List<StudyMemberAssignmentResponse> {
        val study = findStudy(studyId)
        study.checkMemberByEmail(jwtUtils.extractEmail())

        val members = getTargetMembers(study, memberId)
        val sessions = getTargetSessions(studyId, studySessionId)

        return members.map { member ->
            val assignmentDetails = sessions.mapNotNull { session ->
                session.getAssignment()?.let { assignment ->
                    val assignmentRecord = assignment.records.find { it.user.id == member.id }
                    val solvedJudges = judgeRepository.findByProblemIdInAndUserIdAndResult(assignment.problemIds, member.id, Result.CORRECT)
                    val solvedProblemIds = solvedJudges.map { it.problemId }.distinct()
                    
                    AssignmentDetail(
                        sessionId = session.id,
                        sessionTitle = session.title,
                        status = assignmentRecord?.status?.name,
                        solvedCount = solvedProblemIds.size,
                        totalCount = assignment.problemIds.size,
                        solvedProblemIds = solvedProblemIds
                    )
                }
            }
            StudyMemberAssignmentResponse(
                user = toUserBriefResponse(member),
                assignments = assignmentDetails
            )
        }
    }

    private fun findStudy(id: Long): Study = studyRepository.findDetailById(id)
        ?: throw CustomException("Study not found", HttpStatus.NOT_FOUND)

    private fun getTargetMembers(study: Study, memberId: Long?): List<User> {
        return if (memberId != null) {
            val user = userRepository.findById(memberId).orElseThrow { CustomException("User not found", HttpStatus.NOT_FOUND) }
            if (study.studyMembers.none { it.user.id == memberId && it.role != StudyMemberRole.PENDING }) {
                throw CustomException("User is not a member of this study", HttpStatus.BAD_REQUEST)
            }
            listOf(user)
        } else {
            study.getAllActiveMembers()
        }
    }

    private fun getTargetSessions(studyId: Long, studySessionId: Long?): List<com.leita.leita.study.domain.StudySession> {
        return if (studySessionId != null) {
            val session = studySessionRepository.findDetailById(studySessionId)
                ?: throw CustomException("Study session not found", HttpStatus.NOT_FOUND)
            if (session.studyId != studyId) {
                throw CustomException("Session does not belong to this study", HttpStatus.BAD_REQUEST)
            }
            listOf(session)
        } else {
            studySessionRepository.findAllByStudyIdOrderByStartDateTimeDesc(studyId)
        }
    }

    private fun toUserBriefResponse(user: User): UserBriefResponse {
        return UserBriefResponse(
            id = user.id,
            name = user.name,
            email = user.email,
            profileImage = user.profileImage
        )
    }
}
