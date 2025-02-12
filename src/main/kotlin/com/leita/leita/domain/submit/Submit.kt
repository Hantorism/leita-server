package com.leita.leita.domain.submit

import com.leita.leita.domain.User
import com.leita.leita.repository.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "submits")
class Submit(
    val problemId: Long,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Enumerated(EnumType.STRING)
    val result: Result,

    @Embedded
    val used: UsedInfo,

    val sizeOfCode: Long,
) : BaseEntity()