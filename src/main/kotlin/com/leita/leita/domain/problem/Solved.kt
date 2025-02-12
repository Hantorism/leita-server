package com.leita.leita.domain.problem

import jakarta.persistence.*

@Embeddable
data class Solved(

    @Column(name = "solved_count")
    val count: Long,

    @Column(name = "solved_rate")
    val rate: Long,
)