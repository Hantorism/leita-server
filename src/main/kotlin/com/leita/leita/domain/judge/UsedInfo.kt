package com.leita.leita.domain.judge

import jakarta.persistence.*

@Embeddable
open class UsedInfo(
    // kb 기준
    @Column(name = "used_memory", nullable = true)
    val memory: Long?,

    // ms 기준
    @Column(name = "used_time", nullable = true)
    val time: Long?,

    @Column(name = "used_language")
    @Enumerated(EnumType.STRING)
    val language: Language
)