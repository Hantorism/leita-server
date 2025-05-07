package com.leita.leita.service

import com.leita.leita.common.exception.CustomException
import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.controller.dto.study.StudyMapper
import com.leita.leita.controller.dto.study.request.StudyCreateRequest
import com.leita.leita.controller.dto.study.request.StudyUpdateRequest
import com.leita.leita.controller.dto.study.response.StudiesResponse
import com.leita.leita.controller.dto.study.response.StudyCreateResponse
import com.leita.leita.controller.dto.study.response.StudyDetailResponse
import com.leita.leita.domain.study.Study
import com.leita.leita.repository.StudyClassRepository
import com.leita.leita.repository.StudyRepository
import com.leita.leita.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class StudyService(
    private val studyRepository: StudyRepository,
    private val studyClassRepository: StudyClassRepository,
    private val userRepository: UserRepository,
    private val jwtUtils: JwtUtils,
) {

    fun getAll(classId: Long, page: Int, size: Int): StudiesResponse {
        val pageable: Pageable = PageRequest.of(page, size)
        val studies = studyRepository.findStudyByStudyClassId(classId, pageable)
        return StudyMapper.toStudiesResponse(studies)
    }

    fun get(id: Long): StudyDetailResponse {
        val study: Study = studyRepository.findById(id)
            .orElseThrow { CustomException("Study not found", HttpStatus.NOT_FOUND) }
        return StudyMapper.toStudyDetailResponse(study)
    }

    fun update(id: Long, request: StudyUpdateRequest) {
        val adminEmail = jwtUtils.extractEmail()
        val study = studyRepository.findById(id)
            .orElseThrow { CustomException("Study not found", HttpStatus.NOT_FOUND) }
        val studyClass = studyClassRepository.findById(study.studyClassId)
            .orElseThrow { CustomException("Study Class not found", HttpStatus.NOT_FOUND) }
        if (!studyClass.isAdminByEmail(adminEmail)) {
            throw CustomException("Permission denied", HttpStatus.FORBIDDEN)
        }

        study.update(request.startDateTime, request.endDateTime)
        studyRepository.save(study)
    }

    fun delete(id: Long) {
        val adminEmail = jwtUtils.extractEmail()
        val study = studyRepository.findById(id)
            .orElseThrow { CustomException("Study not found", HttpStatus.NOT_FOUND) }
        val studyClass = studyClassRepository.findById(study.studyClassId)
            .orElseThrow { CustomException("Study Class not found", HttpStatus.NOT_FOUND) }
        if (!studyClass.isAdminByEmail(adminEmail)) {
            throw CustomException("Permission denied", HttpStatus.FORBIDDEN)
        }

        studyRepository.deleteById(id)
    }

    fun create(classId: Long, request: StudyCreateRequest): StudyCreateResponse {
        val adminEmail = jwtUtils.extractEmail()
        val studyClass = studyClassRepository.findById(classId)
            .orElseThrow { CustomException("Study Class not found", HttpStatus.NOT_FOUND) }
        if (!studyClass.isAdminByEmail(adminEmail)) {
            throw CustomException("Permission denied", HttpStatus.FORBIDDEN)
        }

        val study = Study.create(
            startDateTime = request.startDateTime,
            endDateTime = request.endDateTime,
            studyClassId = classId,
        )
        studyRepository.save(study)

        return StudyMapper.toStudyCreateResponse(studyClass.id)
    }


    fun attend(id: Long) {
        val email = jwtUtils.extractEmail()
        val member = userRepository.findById(id)
            .orElseThrow { CustomException("Member not found", HttpStatus.NOT_FOUND) }
        val study = studyRepository.findById(id)
            .orElseThrow { CustomException("Study not found", HttpStatus.NOT_FOUND) }
        val studyClass = studyClassRepository.findById(study.studyClassId)
            .orElseThrow { CustomException("Study Class not found", HttpStatus.NOT_FOUND) }
        if (!studyClass.isMemberByEmail(email)) {
            throw CustomException("Permission denied", HttpStatus.FORBIDDEN)
        }

        study.attend(member)
        studyRepository.save(study)
    }
}