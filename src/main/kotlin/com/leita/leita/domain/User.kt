package com.leita.leita.domain

import com.leita.leita.controller.dto.auth.request.LoginRequest
import com.leita.leita.controller.dto.auth.request.RegisterRequest
import com.leita.leita.repository.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Column(nullable = false)
    val username: String,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val password: String,
) : BaseEntity()