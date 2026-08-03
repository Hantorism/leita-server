package com.leita.leita.user.controller

import com.leita.leita.common.dto.BaseResponse
import com.leita.leita.user.dto.AffiliationRequest
import com.leita.leita.user.dto.AffiliationResponse
import com.leita.leita.user.service.AdminAffiliationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/affiliations")
class AdminAffiliationController(
    private val adminAffiliationService: AdminAffiliationService
) {
    @GetMapping
    fun getAffiliations(): ResponseEntity<BaseResponse<List<AffiliationResponse>>> {
        val affiliations = adminAffiliationService.getAffiliations()
        return ResponseEntity.ok(BaseResponse("소속 목록 조회 완료", affiliations))
    }

    @PostMapping
    fun createAffiliation(@RequestBody request: AffiliationRequest): ResponseEntity<BaseResponse<AffiliationResponse>> {
        val affiliation = adminAffiliationService.createAffiliation(request)
        return ResponseEntity.ok(BaseResponse("소속 추가 완료", affiliation))
    }

    @PutMapping("/{id}")
    fun updateAffiliation(
        @PathVariable id: Long,
        @RequestBody request: AffiliationRequest
    ): ResponseEntity<BaseResponse<AffiliationResponse>> {
        val affiliation = adminAffiliationService.updateAffiliation(id, request)
        return ResponseEntity.ok(BaseResponse("소속 수정 완료", affiliation))
    }

    @DeleteMapping("/{id}")
    fun deleteAffiliation(@PathVariable id: Long): ResponseEntity<BaseResponse<Unit>> {
        adminAffiliationService.deleteAffiliation(id)
        return ResponseEntity.ok(BaseResponse("소속 삭제 완료", Unit))
    }
}
