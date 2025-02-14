package com.leita.leita.common.security.jwt

import com.leita.leita.common.config.JwtConfig
import com.leita.leita.controller.dto.auth.response.JwtResponse
import com.leita.leita.port.cache.CachePort
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.Claims
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import java.nio.charset.StandardCharsets
import java.security.Key
import java.time.Instant
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import kotlin.math.max

@Component
class JwtUtils(
    private val jwtProperties: JwtConfig,
    private val cachePort: CachePort,
    private val request: HttpServletRequest
) {

    fun generateToken(email: String): JwtResponse {
        val jti = UUID.randomUUID().toString()
        val key: Key = SecretKeySpec(jwtProperties.secret.toByteArray(), SignatureAlgorithm.HS512.getJcaName())

        val accessToken: String = Jwts.builder()
            .setSubject(email)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration.toLong()))
            .signWith(key)
            .setId(jti)
            .compact()

        val refreshToken: String = Jwts.builder()
            .setSubject(email)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpiration.toLong()))
            .signWith(key)
            .setId(jti)
            .compact()

        return JwtResponse(accessToken, refreshToken)
    }

    fun disableToken(): Boolean {
        val token = token
        val remainingTime = remainingTime
        if (remainingTime == 0L) {
            return false
        }

        cachePort.set(extractJTI(), token)
        return cachePort.get(extractJTI()) == token
    }

    fun extractEmail(): String {
        return extractAllClaims().subject
    }

    private fun extractJTI(): String {
        return extractAllClaims().id
    }

    private fun extractExpirationTime(): Date {
        return extractAllClaims().expiration
    }

    private val remainingTime: Long
        get() {
            val currentTimeMillis = Instant.now().toEpochMilli()
            val expirationTime = extractExpirationTime()
            val remainingTimeMillis = expirationTime.time - currentTimeMillis
            return max(remainingTimeMillis.toDouble(), 0.0).toLong()
        }

    private fun extractAllClaims(): Claims {
        val secretKey: SecretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray(StandardCharsets.UTF_8))
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
    }

    fun isValidateToken(email: String): Boolean {
        val extractedEmail = extractEmail()
        return (extractedEmail == email && !isTokenExpired)
    }

    val isTokenExpired: Boolean
        get() {
            return extractExpirationTime().before(Date(System.currentTimeMillis())) ||
                    cachePort.get(extractJTI()) != null
        }

    private val token: String
        get() {
            val token = request.getHeader("Authorization")
                ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "토큰 정보를 확인해주세요!")

            if (token.startsWith("Bearer ")) {
                return token.substring(7).trim { it <= ' ' }
            }

            return token
        }
}
