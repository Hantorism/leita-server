package com.leita.leita.domain.user

import com.fasterxml.jackson.annotation.JsonIgnore
import com.leita.leita.common.exception.CustomException
import com.leita.leita.port.google.dto.OAuthUserInfo
import com.leita.leita.common.security.SecurityRole
import com.leita.leita.domain.BaseEntity
import jakarta.persistence.Access
import jakarta.persistence.AccessType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import org.springframework.http.HttpStatus

@Entity
@Table(name = "users")
@Access(AccessType.FIELD)
open class User(
    @Column(nullable = false, unique = true)
    open var name: String,

    @Column(nullable = false, unique = true)
    open var email: String,

    @Column(nullable = true)
    open var profileImage: String?,

    @Column(nullable = true)
    open var githubInfo: GithubInfo?,

    @Column(nullable = true)
    open var installationId: Long?,

    @JsonIgnore
    @Column(nullable = false)
    open var sub: String,

    @JsonIgnore
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    open var role: SecurityRole,
) : BaseEntity() {
    companion object {
        fun oauthLogin(oAuthUserInfo: OAuthUserInfo): User {
            isAjouEmail(oAuthUserInfo.email)
            return User(
                name = oAuthUserInfo.name,
                email = oAuthUserInfo.email,
                profileImage = oAuthUserInfo.picture,
                githubInfo = null,
                installationId = null,
                sub = oAuthUserInfo.sub,
                role = SecurityRole.USER
            )
        }

        private fun isAjouEmail(email: String) {
            if(!email.endsWith("@ajou.ac.kr")) {
                throw CustomException("Use only Ajou Univ. email address", HttpStatus.BAD_REQUEST)
            }
        }
    }

}