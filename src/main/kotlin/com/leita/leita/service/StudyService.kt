package com.leita.leita.service

import com.leita.leita.common.exception.CustomException
import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.controller.dto.auth.StudyMapper
import com.leita.leita.controller.dto.problem.response.StudiesResponse
import com.leita.leita.controller.dto.study.request.StudyCreateRequest
import com.leita.leita.controller.dto.study.request.StudyRoleChangeRequest
import com.leita.leita.controller.dto.study.response.*
import com.leita.leita.domain.study.Study
import com.leita.leita.domain.User
import com.leita.leita.domain.study.StudyRole
import com.leita.leita.port.mail.MailPort
import com.leita.leita.port.mail.MailType
import com.leita.leita.repository.MemberRepository
import com.leita.leita.repository.PendingRepository
import com.leita.leita.repository.StudyRepository
import com.leita.leita.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class StudyService(
    private val studyRepository: StudyRepository,
    private val pendingRepository: PendingRepository,
    private val userRepository: UserRepository,
    private val jwtUtils: JwtUtils,
    private val mailPort: MailPort,
    private val memberRepository: MemberRepository
) {

    fun getStudies(page: Int, size: Int): StudiesResponse {
        val pageable: Pageable = PageRequest.of(page, size)
        val studies = studyRepository.findAll(pageable)
        return StudyMapper.toStudiesResponse(studies)
    }

    fun getStudy(id: Long): StudyDetailResponse {
        val study: Study = studyRepository.findById(id)
            .orElseThrow { CustomException("Study not found", HttpStatus.NOT_FOUND) }
        return StudyMapper.toStudyDetailResponse(study)
    }

    fun getStudyMembers(id: Long, page: Int, size: Int): StudyPendingResponse {
        studyRepository.findById(id).orElseThrow {
            throw CustomException("Study not found", HttpStatus.NOT_FOUND)
        }

        val pageable: Pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val members: Page<User> = memberRepository.findMembersByStudyId(id, pageable)

        return StudyMapper.toStudyPendingResponse(members)
    }

    fun changeRole(id: Long, request: StudyRoleChangeRequest): StudyRoleChangeResponse {
        val adminEmail = jwtUtils.extractEmail()
        val study = studyRepository.findById(id)
            .orElseThrow { CustomException("Study not found", HttpStatus.NOT_FOUND) }
        if (!study.isAdminByEmail(adminEmail)) {
            throw CustomException("Permission denied", HttpStatus.FORBIDDEN)
        }

        val user = userRepository.findByEmail(request.email)
            ?: throw CustomException("User not found", HttpStatus.UNAUTHORIZED)
        val result = study.changeRole(user, request.newRole)

        return StudyRoleChangeResponse(result)
    }

    fun create(request: StudyCreateRequest): StudyCreateResponse {
        val admins = request.adminEmails.mapNotNull(userRepository::findByEmail).toMutableList()
        val pending = request.pendingEmails.mapNotNull(userRepository::findByEmail).toMutableList()

        val study = studyRepository.save(
            Study(
                admins,
                title = request.title,
                description = request.description,
                pending
            )
        )

        request.adminEmails.forEach { mailPort.send(MailType.STUDY_ADMIN_INVITE, it) }
        request.pendingEmails.forEach { mailPort.send(MailType.STUDY_MEMBER_INVITE, it) }

        return StudyMapper.toStudyCreateResponse(study.id)
    }

    fun join(id: Long): StudyJoinResponse {
        val email: String = jwtUtils.extractEmail()
        val user: User? = userRepository.findByEmail(email)

        val study: Study = studyRepository.findById(id).get()

        if(user != null) {
            study.join(user)
            return StudyJoinResponse(true)
        }
        return StudyJoinResponse(false)
    }

    fun pending(id: Long, page: Int, size: Int): StudyPendingResponse {
        val email: String = jwtUtils.extractEmail()

        val study: Study = studyRepository.findById(id).orElseThrow {
            throw CustomException("Study not found", HttpStatus.NOT_FOUND)
        }

        if (!study.isAdminByEmail(email)) {
            throw CustomException("Permission denied", HttpStatus.FORBIDDEN)
        }

        val pageable: Pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val pending: Page<User> = pendingRepository.findPendingByStudyId(id, pageable)

        return StudyMapper.toStudyPendingResponse(pending)
    }

    fun approve(id: Long, email: String): StudyApproveResponse {
        val adminEmail = jwtUtils.extractEmail()
        val study: Study = studyRepository.findById(id).get()

        if(study.isAdminByEmail(adminEmail)) {
            val user: User? = userRepository.findByEmail(email)
            if(user != null) {
                study.approve(user)
                return StudyApproveResponse(true)
            }
        }
        return StudyApproveResponse(false)
    }

    fun deny(id: Long, email: String): StudyDenyResponse {
        val adminEmail = jwtUtils.extractEmail()
        val study: Study = studyRepository.findById(id).get()

        if(study.isAdminByEmail(adminEmail)) {
            val user: User = userRepository.findByEmail(email)
                ?: throw CustomException("User not found", HttpStatus.UNAUTHORIZED)
            study.deny(user)
            return StudyDenyResponse(true)
        }
        throw CustomException("Permission denied", HttpStatus.FORBIDDEN)
    }

    fun leave(id: Long): StudyLeaveResponse {
        val memberEmail = jwtUtils.extractEmail()
        val study: Study = studyRepository.findById(id).get()

        if(study.isMemberByEmail(memberEmail)) {
            val user: User = userRepository.findByEmail(memberEmail)
                ?: throw CustomException("User not found", HttpStatus.UNAUTHORIZED)
            study.leave(user)
            return StudyLeaveResponse(true)
        }
        throw CustomException("Permission denied", HttpStatus.FORBIDDEN)
    }
}