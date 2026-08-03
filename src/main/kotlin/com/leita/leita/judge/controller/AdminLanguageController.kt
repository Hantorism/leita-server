package com.leita.leita.judge.controller

import com.leita.leita.common.dto.BaseResponse
import com.leita.leita.judge.dto.LanguageRequest
import com.leita.leita.judge.dto.LanguageResponse
import com.leita.leita.judge.service.AdminLanguageService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/languages")
class AdminLanguageController(
    private val adminLanguageService: AdminLanguageService
) {
    @GetMapping
    fun getLanguages(): ResponseEntity<BaseResponse<List<LanguageResponse>>> {
        val languages = adminLanguageService.getLanguages()
        return ResponseEntity.ok(BaseResponse("언어 목록 조회 완료", languages))
    }

    @PostMapping
    fun createLanguage(@RequestBody request: LanguageRequest): ResponseEntity<BaseResponse<LanguageResponse>> {
        val language = adminLanguageService.createLanguage(request)
        return ResponseEntity.ok(BaseResponse("언어 추가 완료", language))
    }

    @PutMapping("/{id}")
    fun updateLanguage(
        @PathVariable id: Long,
        @RequestBody request: LanguageRequest
    ): ResponseEntity<BaseResponse<LanguageResponse>> {
        val language = adminLanguageService.updateLanguage(id, request)
        return ResponseEntity.ok(BaseResponse("언어 수정 완료", language))
    }

    @DeleteMapping("/{id}")
    fun deleteLanguage(@PathVariable id: Long): ResponseEntity<BaseResponse<Unit>> {
        adminLanguageService.deleteLanguage(id)
        return ResponseEntity.ok(BaseResponse("언어 삭제 완료", Unit))
    }
}
