package com.leita.leita.service

import com.leita.leita.common.exception.CustomException
import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.controller.study.StudyMapper
import com.leita.leita.controller.study.request.StudyCreateRequest
import com.leita.leita.controller.study.request.StudyMemberRequest
import com.leita.leita.controller.study.request.StudyRoleChangeRequest
import com.leita.leita.controller.study.request.StudyUpdateRequest
import com.leita.leita.controller.study.response.StudyCreateResponse
import com.leita.leita.controller.study.response.StudyDetailResponse
import com.leita.leita.controller.study.response.StudyMemberResponse
import com.leita.leita.controller.study.response.StudiesResponse
import com.leita.leita.domain.study.Study
import com.leita.leita.domain.study.StudyMemberRole
import com.leita.leita.domain.user.User
import com.leita.leita.repository.StudyMemberRepository
import com.leita.leita.repository.StudyRepository
import com.leita.leita.repository.UserRepository
import com.leita.leita.util.mail.MailType
import com.leita.leita.util.mail.MailUtil
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class StudyService(
    private val studyRepository: StudyRepository,
    private val studyMemberRepository: StudyMemberRepository,
    private val userRepository: UserRepository,
    private val jwtUtils: JwtUtils,
    private val mailUtil: MailUtil,
) {

    fun getStudies(page: Int, size: Int): StudiesResponse {
        val pageable: Pageable = PageRequest.of(page, size)
        val studies = studyRepository.findAll(pageable)
        return StudyMapper.toStudiesResponse(studies)
    }

    fun getStudy(id: Long): StudyDetailResponse {
        val study = studyRepository.findDetailById(id)
            ?: throw CustomException("Study not found", HttpStatus.NOT_FOUND)
        return StudyMapper.toStudyDetailResponse(study)
    }

    fun updateStudy(id: Long, request: StudyUpdateRequest): StudyDetailResponse {
        val adminEmail = jwtUtils.extractEmail()
        val study = studyRepository.findDetailById(id)
            ?: throw CustomException("Study not found", HttpStatus.NOT_FOUND)
        study.checkAdminByEmail(adminEmail)

        study.update(
            title = request.title,
            description = request.description,
            requirement = request.requirement,
            startDate = request.startDate,
            endDate = request.endDate,
            attendanceRequired = request.attendanceRequired,
            assignmentRequired = request.assignmentRequired,
            requiredAttendanceCount = request.requiredAttendanceCount,
            requiredAssignmentCount = request.requiredAssignmentCount
        )
        studyRepository.save(study)
        return StudyMapper.toStudyDetailResponse(study)
    }

    fun deleteStudy(id: Long) {
        val adminEmail = jwtUtils.extractEmail()
        val study = studyRepository.findDetailById(id)
            ?: throw CustomException("Study not found", HttpStatus.NOT_FOUND)
        study.checkAdminByEmail(adminEmail)

        studyRepository.deleteById(id)
    }

    fun getStudyMembers(id: Long, role: StudyMemberRole): List<User> {
        val study = studyRepository.findDetailById(id)
            ?: throw CustomException("Study not found", HttpStatus.NOT_FOUND)

        return when (role) {
            StudyMemberRole.ADMIN -> study.getAdminUsers()
            StudyMemberRole.MEMBER -> study.getRegularUsers()
            StudyMemberRole.PENDING -> study.getPendingUsers()
        }
    }

    fun changeRole(id: Long, request: StudyRoleChangeRequest) {
        val adminEmail = jwtUtils.extractEmail()
        val study = studyRepository.findDetailById(id)
            ?: throw CustomException("Study not found", HttpStatus.NOT_FOUND)
        study.checkAdminByEmail(adminEmail)

        val user = userRepository.findByEmail(request.email)
            ?: throw CustomException("User not found", HttpStatus.UNAUTHORIZED)

        study.changeRole(user, request.newRole)
        studyRepository.save(study)
    }

    fun create(request: StudyCreateRequest): StudyCreateResponse {
        val admin = jwtUtils.extractUser()
        val study = Study.create(
            title = request.title,
            description = request.description,
            requirement = request.requirement,
            startDate = request.startDate,
            endDate = request.endDate,
            admin = admin,
            attendanceRequired = request.attendanceRequired,
            assignmentRequired = request.assignmentRequired,
            requiredAttendanceCount = request.requiredAttendanceCount,
            requiredAssignmentCount = request.requiredAssignmentCount
        )
        studyRepository.save(study)

        return StudyMapper.toStudyCreateResponse(study)
    }

    fun join(id: Long) {
        val user = jwtUtils.extractUser()
        val study = studyRepository.findDetailById(id)
            ?: throw CustomException("Study not found", HttpStatus.NOT_FOUND)

        study.join(user)
        studyRepository.save(study)
        mailUtil.sendAll(MailType.STUDY_MEMBER_JOIN, study.getAdminUsers().map { it.email })
    }

    fun approve(id: Long, request: StudyMemberRequest) {
        val adminEmail = jwtUtils.extractEmail()
        val study = studyRepository.findDetailById(id)
            ?: throw CustomException("Study not found", HttpStatus.NOT_FOUND)

        study.checkAdminByEmail(adminEmail)
        val user = userRepository.findByEmail(request.email)
            ?: throw CustomException("User not found", HttpStatus.NOT_FOUND)
        study.approve(user)
        studyRepository.save(study)
    }

    fun deny(id: Long, request: StudyMemberRequest) {
        val adminEmail = jwtUtils.extractEmail()
        val study = studyRepository.findDetailById(id)
            ?: throw CustomException("Study not found", HttpStatus.NOT_FOUND)

        study.checkAdminByEmail(adminEmail)
        val user = userRepository.findByEmail(request.email)
            ?: throw CustomException("User not found", HttpStatus.UNAUTHORIZED)
        study.deny(user)
        studyRepository.save(study)
    }

    fun leave(id: Long) {
        val memberEmail = jwtUtils.extractEmail()
        val study = studyRepository.findDetailById(id)
            ?: throw CustomException("Study not found", HttpStatus.NOT_FOUND)

        study.checkMemberByEmail(memberEmail)
        val user = userRepository.findByEmail(memberEmail)
            ?: throw CustomException("User not found", HttpStatus.UNAUTHORIZED)
        study.leave(user)
        studyRepository.save(study)
    }

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
}
