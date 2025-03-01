package com.leita.leita.domain.submit

import com.leita.leita.domain.User
import com.leita.leita.repository.BaseEntity
import jakarta.annotation.Nullable
import jakarta.persistence.*

@Entity
@Table(name = "submits")
class Submit(

    @Column(nullable = false)
    val problemId: Long,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Nullable
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    val result: Result? = null,

    @Nullable
    @Embedded
    @Column(nullable = true)
    val used: UsedInfo? = null,

    @Nullable
    @Column(nullable = true)
    val sizeOfCode: Long? = null,

    @Column(nullable = false)
    val type: SubmitType
) : BaseEntity()