package com.leita.leita.judge.service

import com.leita.leita.common.exception.CustomException
import com.leita.leita.judge.domain.Language
import com.leita.leita.judge.dto.LanguageRequest
import com.leita.leita.judge.dto.LanguageResponse
import com.leita.leita.judge.repository.LanguageRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminLanguageService(
    private val languageRepository: LanguageRepository
) {
    @Transactional(readOnly = true)
    fun getLanguages(): List<LanguageResponse> {
        return languageRepository.findAll().map { LanguageResponse.from(it) }
    }

    @Transactional
    fun createLanguage(request: LanguageRequest): LanguageResponse {
        if (languageRepository.existsByName(request.name)) {
            throw CustomException("이미 등록된 언어 이름입니다.", HttpStatus.BAD_REQUEST)
        }
        if (languageRepository.existsByCode(request.code.lowercase())) {
            throw CustomException("이미 등록된 언어 코드입니다.", HttpStatus.BAD_REQUEST)
        }
        val language = languageRepository.save(
            Language(
                name = request.name,
                code = request.code.lowercase(),
                extension = request.extension.lowercase()
            )
        )
        return LanguageResponse.from(language)
    }

    @Transactional
    fun updateLanguage(id: Long, request: LanguageRequest): LanguageResponse {
        val language = languageRepository.findById(id).orElseThrow {
            CustomException("존재하지 않는 언어입니다.", HttpStatus.NOT_FOUND)
        }
        val existingName = languageRepository.findAll().any { it.id != id && it.name == request.name }
        if (existingName) {
            throw CustomException("이미 등록된 언어 이름입니다.", HttpStatus.BAD_REQUEST)
        }
        val existingCode = languageRepository.findAll().any { it.id != id && it.code == request.code.lowercase() }
        if (existingCode) {
            throw CustomException("이미 등록된 언어 코드입니다.", HttpStatus.BAD_REQUEST)
        }

        language.name = request.name
        language.code = request.code.lowercase()
        language.extension = request.extension.lowercase()
        val saved = languageRepository.save(language)
        return LanguageResponse.from(saved)
    }

    @Transactional
    fun deleteLanguage(id: Long) {
        if (!languageRepository.existsById(id)) {
            throw CustomException("존재하지 않는 언어입니다.", HttpStatus.NOT_FOUND)
        }
        languageRepository.deleteById(id)
    }
}
