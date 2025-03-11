package com.leita.leita.domain.judge

import com.leita.leita.domain.User
import com.leita.leita.repository.BaseEntity
import jakarta.annotation.Nullable
import jakarta.persistence.*

@Entity
@Table(name = "judge")
@Access(AccessType.FIELD)
open class Judge(

    @Column(nullable = false)
    open val problemId: Long,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    open val user: User,

    @Nullable
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    open val result: Result? = null,

    @Nullable
    @Embedded
    @Column(nullable = true)
    open val used: UsedInfo? = null,

    @Nullable
    @Column(nullable = true)
    open val sizeOfCode: Long? = null,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    open val type: JudgeType
) : BaseEntity()