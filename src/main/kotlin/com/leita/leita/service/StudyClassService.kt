package com.leita.leita.service

import com.leita.leita.common.exception.CustomException
import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.controller.dto.auth.StudyClassMapper
import com.leita.leita.controller.dto.studyClass.request.StudyClassCreateRequest
import com.leita.leita.controller.dto.studyClass.request.StudyClassRoleChangeRequest
import com.leita.leita.controller.dto.studyClass.request.StudyClassUpdateRequest
import com.leita.leita.controller.dto.studyClass.response.*
import com.leita.leita.domain.study.StudyClass
import com.leita.leita.domain.User
import com.leita.leita.port.mail.MailPort
import com.leita.leita.port.mail.MailType
import com.leita.leita.repository.MemberRepository
import com.leita.leita.repository.StudyClassRepository
import com.leita.leita.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class StudyClassService(
    private val studyClassRepository: StudyClassRepository,
    private val userRepository: UserRepository,
    private val jwtUtils: JwtUtils,
    private val mailPort: MailPort,
    private val memberRepository: MemberRepository
) {

    fun getStudyClasses(page: Int, size: Int): StudyClassesResponse {
        val pageable: Pageable = PageRequest.of(page, size)
        val studies = studyClassRepository.findAll(pageable)
        return StudyClassMapper.toStudyClassesResponse(studies)
    }

    fun getStudyClass(id: Long): StudyClassDetailResponse {
        val study: StudyClass = studyClassRepository.findById(id)
            .orElseThrow { CustomException("Study Class not found", HttpStatus.NOT_FOUND) }
        return StudyClassMapper.toStudyClassDetailResponse(study)
    }

    fun updateStudyClass(id: Long, request: StudyClassUpdateRequest): StudyClassDetailResponse {
        val adminEmail = jwtUtils.extractEmail()
        val studyClass = studyClassRepository.findById(id)
            .orElseThrow { CustomException("Study Class not found", HttpStatus.NOT_FOUND) }
        if (!studyClass.isAdminByEmail(adminEmail)) {
            throw CustomException("Permission denied", HttpStatus.FORBIDDEN)
        }

        studyClass.update(request.title, request.description, request.requirement);
        studyClassRepository.save(studyClass)
        return StudyClassMapper.toStudyClassDetailResponse(studyClass)
    }

    fun deleteStudyClass(id: Long) {
        val adminEmail = jwtUtils.extractEmail()
        val studyClass = studyClassRepository.findById(id)
            .orElseThrow { CustomException("Study Class not found", HttpStatus.NOT_FOUND) }
        if (!studyClass.isAdminByEmail(adminEmail)) {
            throw CustomException("Permission denied", HttpStatus.FORBIDDEN)
        }

        studyClassRepository.deleteById(id)
    }

    fun getStudyMembers(id: Long, page: Int, size: Int): StudyClassPendingResponse {
        studyClassRepository.findById(id).orElseThrow {
            throw CustomException("Study Class not found", HttpStatus.NOT_FOUND)
        }

        val pageable: Pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val members: Page<User> = memberRepository.findMembersByStudyId(id, pageable)

        return StudyClassMapper.toStudyPendingResponse(members)
    }

    fun changeRole(id: Long, request: StudyClassRoleChangeRequest): StudyClassRoleChangeResponse {
        val adminEmail = jwtUtils.extractEmail()
        val study = studyClassRepository.findById(id)
            .orElseThrow { CustomException("Study Class not found", HttpStatus.NOT_FOUND) }
        if (!study.isAdminByEmail(adminEmail)) {
            throw CustomException("Permission denied", HttpStatus.FORBIDDEN)
        }

        val user = userRepository.findByEmail(request.email)
            ?: throw CustomException("User not found", HttpStatus.UNAUTHORIZED)
        val result = study.changeRole(user, request.newRole)

        return StudyClassRoleChangeResponse(result)
    }

    fun create(request: StudyClassCreateRequest): StudyClassCreateResponse {
        val adminEmail = jwtUtils.extractEmail()
        val admin = userRepository.findByEmail(adminEmail)
            ?: throw CustomException("User not found", HttpStatus.UNAUTHORIZED)

        val studyClass = StudyClass.create(
            title = request.title,
            description = request.description,
            requirement = request.requirement,
            admin
        )
        studyClassRepository.save(studyClass)

        return StudyClassMapper.toStudyCreateResponse(studyClass.id)
    }

    fun join(id: Long): StudyClassJoinResponse {
        val email: String = jwtUtils.extractEmail()
        val user: User? = userRepository.findByEmail(email)

        val study: StudyClass = studyClassRepository.findById(id).get()

        if(user != null) {
            study.join(user)
            mailPort.sendAll(MailType.STUDY_MEMBER_JOIN, study.admins.map { it.email })
            return StudyClassJoinResponse(true)
        }
        return StudyClassJoinResponse(false)
    }

    fun pending(id: Long, page: Int, size: Int): StudyClassPendingResponse {
        val email: String = jwtUtils.extractEmail()

        val studyClass: StudyClass = studyClassRepository.findById(id).orElseThrow {
            throw CustomException("Study Class not found", HttpStatus.NOT_FOUND)
        }

        if (!studyClass.isAdminByEmail(email)) {
            throw CustomException("Permission denied", HttpStatus.FORBIDDEN)
        }

        val pageable: Pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val pendings = studyClass.getPendingsPage(pageable)

        return StudyClassMapper.toStudyPendingResponse(pendings)
    }

    fun approve(id: Long, email: String): StudyClassApproveResponse {
        val adminEmail = jwtUtils.extractEmail()
        val study: StudyClass = studyClassRepository.findById(id).get()

        if(study.isAdminByEmail(adminEmail)) {
            val user: User? = userRepository.findByEmail(email)
            if(user != null) {
                study.approve(user)
                return StudyClassApproveResponse(true)
            }
        }
        return StudyClassApproveResponse(false)
    }

    fun deny(id: Long, email: String): StudyClassDenyResponse {
        val adminEmail = jwtUtils.extractEmail()
        val study: StudyClass = studyClassRepository.findById(id).get()

        if(study.isAdminByEmail(adminEmail)) {
            val user: User = userRepository.findByEmail(email)
                ?: throw CustomException("User not found", HttpStatus.UNAUTHORIZED)
            study.deny(user)
            return StudyClassDenyResponse(true)
        }
        throw CustomException("Permission denied", HttpStatus.FORBIDDEN)
    }

    fun leave(id: Long): StudyClassLeaveResponse {
        val memberEmail = jwtUtils.extractEmail()
        val study: StudyClass = studyClassRepository.findById(id).get()

        if(study.isMemberByEmail(memberEmail)) {
            val user: User = userRepository.findByEmail(memberEmail)
                ?: throw CustomException("User not found", HttpStatus.UNAUTHORIZED)
            study.leave(user)
            return StudyClassLeaveResponse(true)
        }
        throw CustomException("Permission denied", HttpStatus.FORBIDDEN)
    }
}