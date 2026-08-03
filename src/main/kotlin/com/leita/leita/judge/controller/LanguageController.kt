package com.leita.leita.judge.controller

import com.leita.leita.common.dto.BaseResponse
import com.leita.leita.judge.dto.LanguageResponse
import com.leita.leita.judge.service.LanguageService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/languages")
class LanguageController(
    private val languageService: LanguageService
) {
    @GetMapping
    fun getLanguages(): ResponseEntity<BaseResponse<List<LanguageResponse>>> {
        val languages = languageService.getLanguages()
        return ResponseEntity.ok(BaseResponse("지원 언어 목록 조회 완료", languages))
    }
}
