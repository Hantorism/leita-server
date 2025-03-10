package com.leita.leita.domain.problem

import jakarta.persistence.*

@Embeddable
open class Solved(

    @Column(name = "solved_count")
    val count: Long,

    @Column(name = "solved_rate")
    val rate: Long,
)