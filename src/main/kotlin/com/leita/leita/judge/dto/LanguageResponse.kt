package com.leita.leita.judge.dto

import com.leita.leita.judge.domain.Language

data class LanguageResponse(
    val id: Long,
    val name: String,
    val code: String,
    val extension: String
) {
    companion object {
        fun from(language: Language): LanguageResponse {
            return LanguageResponse(
                id = language.id,
                name = language.name,
                code = language.code,
                extension = language.extension
            )
        }
    }
}
