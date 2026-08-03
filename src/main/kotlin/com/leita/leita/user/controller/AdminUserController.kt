package com.leita.leita.user.controller

import com.leita.leita.common.dto.BaseResponse
import com.leita.leita.user.dto.UserResponse
import com.leita.leita.user.service.AdminUserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/users")
class AdminUserController(
    private val adminUserService: AdminUserService
) {
    @GetMapping
    fun getUsers(
        @RequestParam(required = false) affiliationId: Long?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<BaseResponse<Page<UserResponse>>> {
        val pageable = PageRequest.of(page, size)
        val users = adminUserService.getUsers(affiliationId, pageable)
        return ResponseEntity.ok(BaseResponse("유저 목록 조회 완료", users))
    }

    @PutMapping("/{id}/role")
    fun updateUserRole(
        @PathVariable id: Long,
        @RequestParam role: String
    ): ResponseEntity<BaseResponse<UserResponse>> {
        val user = adminUserService.updateUserRole(id, role)
        return ResponseEntity.ok(BaseResponse("유저 권한 수정 완료", user))
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<BaseResponse<Unit>> {
        adminUserService.deleteUser(id)
        return ResponseEntity.ok(BaseResponse("유저 삭제 완료", Unit))
    }
}
