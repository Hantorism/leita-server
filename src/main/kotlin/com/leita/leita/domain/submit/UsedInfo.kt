package com.leita.leita.domain.submit

import com.leita.leita.domain.enum.Language
import jakarta.persistence.*

@Embeddable
data class UsedInfo(
    val memory: Long,
    val time: Long,
    @Enumerated(EnumType.STRING)
    val language: Language
)