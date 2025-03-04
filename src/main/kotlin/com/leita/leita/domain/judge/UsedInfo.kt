package com.leita.leita.domain.judge

import com.leita.leita.domain.enum.Language
import jakarta.persistence.*

@Embeddable
data class UsedInfo(
    @Column(name = "used_memory", nullable = true)
    val memory: Long?,
    @Column(name = "used_time", nullable = true)
    val time: Long?,
    @Column(name = "used_language")
    @Enumerated(EnumType.STRING)
    val language: Language
)