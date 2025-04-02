package com.leita.leita.domain.problem

import jakarta.persistence.*

@Embeddable
open class Limit(

    @Column(name = "limit_memory")
    val memory: Long,

    @Column(name = "limit_time")
    val time: Long,
)