package com.leita.leita.user.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.leita.leita.common.exception.CustomException
import com.leita.leita.util.google.dto.OAuthUserInfo
import com.leita.leita.common.security.SecurityRole
import com.leita.leita.common.domain.BaseEntity
import jakarta.persistence.Access
import jakarta.persistence.AccessType
import jakarta.persistence.AttributeOverride
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import org.springframework.http.HttpStatus
import kotlin.random.Random

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

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "githubUserName", column = Column(name = "github_user_name", nullable = true)),
        AttributeOverride(name = "installationId", column = Column(name = "installation_id", nullable = true))
    )
    open var githubInfo: GithubInfo?,

    @JsonIgnore
    @Column(nullable = false)
    open var sub: String,

    @JsonIgnore
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    open var role: SecurityRole,

    @Column(nullable = true)
    open var mainLanguage: String? = null,

    @Column(nullable = true)
    open var department: String? = null,
) : BaseEntity() {

    fun updateInfo(name: String?, profileImage: String?, mainLanguage: String?, department: String?) {
        name?.let { this.name = it }
        profileImage?.let { this.profileImage = it }
        this.mainLanguage = mainLanguage
        this.department = department
    }

    fun addGithubApps(installationId: Long, githubUserName: String): User {
        this.githubInfo = GithubInfo(githubUserName, installationId)
        return this
    }

    companion object {
        fun oauthLogin(oAuthUserInfo: OAuthUserInfo): User {
            isAjouEmail(oAuthUserInfo.email)
            return User(
                name = oAuthUserInfo.name,
                email = oAuthUserInfo.email,
                profileImage = oAuthUserInfo.picture,
                githubInfo = null,
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

    fun generateGitState(): Long {
        return Random.nextInt(10000, 100000).toLong()
    }
}