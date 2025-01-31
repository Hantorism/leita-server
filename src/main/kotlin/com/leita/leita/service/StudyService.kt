package com.leita.leita.service

import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.controller.dto.auth.StudyMapper
import com.leita.leita.controller.dto.study.request.StudyCreateRequest
import com.leita.leita.controller.dto.study.response.*
import com.leita.leita.domain.Study
import com.leita.leita.domain.User
import com.leita.leita.repository.StudyRepository
import com.leita.leita.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class StudyService(
    private val studyRepository: StudyRepository,
    private val userRepository: UserRepository,
    private val jwtUtils: JwtUtils,
//    private val mailPort: GmailPort
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
        val admins: MutableList<User> = request.adminEmails.map {
            userRepository.findByEmail(it)!!
        }.toMutableList()

        val pending: MutableList<User> = request.pendingEmails.map {
            userRepository.findByEmail(it)!!
        }.toMutableList()

        val study = Study(
            admins,
            title = request.title,
            description = request.description,
            pending,
        )

        val id: Long = studyRepository.save(study).id

        // 초대 요청 메일 전송
        // request.adminEmails.map {}
        // request.pendingEmails.map {}

        return StudyMapper.toStudyCreateResponse(id)
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