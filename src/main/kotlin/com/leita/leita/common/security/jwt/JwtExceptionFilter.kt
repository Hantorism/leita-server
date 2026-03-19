package com.leita.leita.common.security.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import com.leita.leita.common.exception.ErrorResponse
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtExceptionFilter(
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        try {
            chain.doFilter(request, response)
        } catch (e: ExpiredJwtException) {
            setErrorResponse(response, "토큰이 만료되었습니다.")
        } catch (e: JwtException) {
            setErrorResponse(response, "유효하지 않은 토큰입니다.")
        }
    }

    private fun setErrorResponse(response: HttpServletResponse, message: String) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = "application/json; charset=UTF-8"
        val errorResponse = ErrorResponse(message = message)
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}
