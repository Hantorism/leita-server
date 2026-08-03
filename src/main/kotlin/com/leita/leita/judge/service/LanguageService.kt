package com.leita.leita.judge.service

import com.leita.leita.judge.dto.LanguageResponse
import com.leita.leita.judge.repository.LanguageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LanguageService(
    private val languageRepository: LanguageRepository
) {
    @Transactional(readOnly = true)
    fun getLanguages(): List<LanguageResponse> {
        return languageRepository.findAll().map { LanguageResponse.from(it) }
    }
}
