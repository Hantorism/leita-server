package com.leita.leita.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.leita.leita.common.security.SecurityRole
import com.leita.leita.repository.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    @Column(nullable = false)
    val username: String,

    @Column(nullable = false, unique = true)
    val email: String,

    @JsonIgnore
    @Column(nullable = false)
    val password: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val role: SecurityRole,
) : BaseEntity()