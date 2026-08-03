package com.leita.leita.user.service

import com.leita.leita.common.exception.CustomException
import com.leita.leita.user.domain.Affiliation
import com.leita.leita.user.dto.AffiliationRequest
import com.leita.leita.user.dto.AffiliationResponse
import com.leita.leita.user.repository.AffiliationRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminAffiliationService(
    private val affiliationRepository: AffiliationRepository
) {
    @Transactional(readOnly = true)
    fun getAffiliations(): List<AffiliationResponse> {
        return affiliationRepository.findAll().map { AffiliationResponse.from(it) }
    }

    @Transactional
    fun createAffiliation(request: AffiliationRequest): AffiliationResponse {
        if (affiliationRepository.existsByName(request.name)) {
            throw CustomException("이미 등록된 소속 이름입니다.", HttpStatus.BAD_REQUEST)
        }
        if (affiliationRepository.existsByEmailDomain(request.emailDomain)) {
            throw CustomException("이미 등록된 이메일 도메인입니다.", HttpStatus.BAD_REQUEST)
        }
        val affiliation = affiliationRepository.save(Affiliation(request.name, request.emailDomain))
        return AffiliationResponse.from(affiliation)
    }

    @Transactional
    fun updateAffiliation(id: Long, request: AffiliationRequest): AffiliationResponse {
        val affiliation = affiliationRepository.findById(id).orElseThrow {
            CustomException("존재하지 않는 소속입니다.", HttpStatus.NOT_FOUND)
        }
        val existingName = affiliationRepository.findAll().any { it.id != id && it.name == request.name }
        if (existingName) {
            throw CustomException("이미 등록된 소속 이름입니다.", HttpStatus.BAD_REQUEST)
        }
        val existingDomain = affiliationRepository.findAll().any { it.id != id && it.emailDomain == request.emailDomain }
        if (existingDomain) {
            throw CustomException("이미 등록된 이메일 도메인입니다.", HttpStatus.BAD_REQUEST)
        }

        affiliation.name = request.name
        affiliation.emailDomain = request.emailDomain
        val saved = affiliationRepository.save(affiliation)
        return AffiliationResponse.from(saved)
    }

    @Transactional
    fun deleteAffiliation(id: Long) {
        if (!affiliationRepository.existsById(id)) {
            throw CustomException("존재하지 않는 소속입니다.", HttpStatus.NOT_FOUND)
        }
        affiliationRepository.deleteById(id)
    }
}
