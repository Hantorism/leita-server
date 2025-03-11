package com.leita.leita.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.leita.leita.common.security.SecurityRole
import com.leita.leita.repository.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "users")
@Access(AccessType.FIELD)
open class User(
    @Column(nullable = false, unique = true)
    open val name: String,

    @Column(nullable = false, unique = true)
    open val email: String,

    @Column(nullable = true)
    open val profileImage: String?,

    @JsonIgnore
    @Column(nullable = false)
    open val sub: String,

    @JsonIgnore
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    open val role: SecurityRole,
) : BaseEntity()