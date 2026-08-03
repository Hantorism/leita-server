package com.leita.leita.user.service

import com.leita.leita.common.exception.CustomException
import com.leita.leita.common.security.SecurityRole
import com.leita.leita.user.dto.UserResponse
import com.leita.leita.user.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminUserService(
    private val userRepository: UserRepository
) {
    @Transactional(readOnly = true)
    fun getUsers(affiliationId: Long?, pageable: Pageable): Page<UserResponse> {
        return if (affiliationId != null) {
            userRepository.findByAffiliationId(affiliationId, pageable).map { UserResponse.from(it) }
        } else {
            userRepository.findAll(pageable).map { UserResponse.from(it) }
        }
    }

    @Transactional
    fun updateUserRole(id: Long, roleStr: String): UserResponse {
        val user = userRepository.findById(id).orElseThrow {
            CustomException("존재하지 않는 유저입니다.", HttpStatus.NOT_FOUND)
        }
        val role = try {
            SecurityRole.valueOf(roleStr.uppercase())
        } catch (e: Exception) {
            throw CustomException("올바르지 않은 권한 역할입니다.", HttpStatus.BAD_REQUEST)
        }
        user.role = role
        val saved = userRepository.save(user)
        return UserResponse.from(saved)
    }

    @Transactional
    fun deleteUser(id: Long) {
        if (!userRepository.existsById(id)) {
            throw CustomException("존재하지 않는 유저입니다.", HttpStatus.NOT_FOUND)
        }
        userRepository.deleteById(id)
    }
}
