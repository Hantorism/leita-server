package com.leita.leita.service

import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.controller.dto.auth.StudyMapper
import com.leita.leita.controller.dto.study.request.StudyCreateRequest
import com.leita.leita.controller.dto.study.response.*
import com.leita.leita.domain.Study
import com.leita.leita.domain.User
import com.leita.leita.port.mail.MailPort
import com.leita.leita.port.mail.MailType
import com.leita.leita.repository.StudyRepository
import com.leita.leita.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class StudyService(
    private val studyRepository: StudyRepository,
    private val userRepository: UserRepository,
    private val jwtUtils: JwtUtils,
    private val mailPort: MailPort
) {

    fun getStudies(): List<StudyDetailResponse> {
        val studies: List<Study> = studyRepository.findAll()
        return studies.map{ StudyMapper.toStudyDetailResponse(it)}
    }

    fun getStudy(id: Long): StudyDetailResponse {
        val study: Study = studyRepository.findById(id).get()
        return StudyMapper.toStudyDetailResponse(study)
    }

    fun create(request: StudyCreateRequest): StudyCreateResponse {
        val admins = request.adminEmails.mapNotNull(userRepository::findByEmail)
        val pending = request.pendingEmails.mapNotNull(userRepository::findByEmail)

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
        val study: Study = studyRepository.findById(id).get()

        val email: String = jwtUtils.extractEmail()
        val user: User? = userRepository.findByEmail(email)

        if(user != null) {
            study.join(user)
            return StudyJoinResponse(true)
        }
        return StudyJoinResponse(false)
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
            val user: User? = userRepository.findByEmail(email)
            if(user != null) {
                study.deny(user)
                return StudyDenyResponse(true)
            }
        }

        return StudyDenyResponse(false)

    }

    fun leave(id: Long): StudyLeaveResponse {
        val study: Study = studyRepository.findById(id).get()

        val email: String = jwtUtils.extractEmail()
        val user: User? = userRepository.findByEmail(email)

        if(user != null) {
            study.leave(user)
            return StudyLeaveResponse(true)
        }
        return StudyLeaveResponse(false)
    }
}