package com.leita.leita.problem.domain

import jakarta.persistence.*

@Embeddable
open class Limit(

    @Column(name = "limit_memory")
    val memory: Long,

    @Column(name = "limit_time")
    val time: Long,
)