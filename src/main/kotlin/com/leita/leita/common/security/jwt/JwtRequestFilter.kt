package com.leita.leita.common.security.jwt

import com.leita.leita.common.config.WebConfig
import com.leita.leita.common.security.ApiPaths
import com.leita.leita.common.security.CustomUserDetailsService
import com.leita.leita.controller.dto.BasicResponse
import jakarta.servlet.ServletException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtRequestFilter(
    private val jwtUtil: JwtUtils,
    private val customUserDetailsService: CustomUserDetailsService,
    private val webConfig: WebConfig
) : OncePerRequestFilter() {

    @Throws(ServletException::class, java.io.IOException::class)
    override fun doFilterInternal(
        request: jakarta.servlet.http.HttpServletRequest,
        response: jakarta.servlet.http.HttpServletResponse,
        chain: jakarta.servlet.FilterChain
    ) {
        val path = request.requestURI.replace(webConfig.contextPath, "")

        try {
            val authorizationHeader = request.getHeader("Authorization")
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                if (isAuthenticated(path)) {
                    val userDetails: UserDetails = customUserDetailsService.loadUserByUsername(
                        jwtUtil.extractEmail()
                    )

                    val usernamePasswordAuthenticationToken =
                        UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                    usernamePasswordAuthenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
                }
            }
        } catch (e: Exception) {
            throw Exception(e)
        } finally {
            chain.doFilter(request, response)
        }
    }

    private fun isAuthenticated(path: String): Boolean {
        return ApiPaths.AUTHENTICATED_ENDPOINTS.any { regex ->
            path.matches(regex.toRegex())
        } && SecurityContextHolder.getContext().authentication == null && !jwtUtil.isTokenExpired
    }
}