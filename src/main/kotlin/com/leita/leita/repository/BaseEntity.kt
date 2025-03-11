package com.leita.leita.repository

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime

@MappedSuperclass
@Access(AccessType.FIELD)
abstract class BaseEntity {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long = 0L

    @JsonIgnore
    @Column(nullable = false, updatable = false)
    open val createdAt: LocalDateTime = LocalDateTime.now()

    @JsonIgnore
    @Column(nullable = false)
    open var updatedAt: LocalDateTime = LocalDateTime.now()

    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }
}
