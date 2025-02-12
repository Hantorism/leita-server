package com.leita.leita.domain.submit

import com.leita.leita.domain.enum.Language
import jakarta.persistence.*

@Embeddable
data class UsedInfo(
    @Column(name = "used_memory")
    val memory: Long,
    @Column(name = "used_time")
    val time: Long,
    @Column(name = "used_language")
    @Enumerated(EnumType.STRING)
    val language: Language
)