package com.leita.leita.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.leita.leita.common.security.SecurityRole
import com.leita.leita.repository.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "users")
open class User(
    @Column(nullable = false, unique = true)
    val name: String,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = true)
    val profileImage: String?,

    @JsonIgnore
    @Column(nullable = false)
    val sub: String,

    @JsonIgnore
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val role: SecurityRole,
) : BaseEntity()