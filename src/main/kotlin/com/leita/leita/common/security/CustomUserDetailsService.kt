package com.leita.leita.common.security

import com.leita.leita.domain.User
import com.leita.leita.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val user: User = userRepository.findByEmail(username)
            ?: throw UsernameNotFoundException("User not found with email: $username")
        return CustomUserDetails(user.name, user.sub, SecurityRole.USER)
    }
}
