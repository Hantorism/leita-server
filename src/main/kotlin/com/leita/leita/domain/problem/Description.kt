package com.leita.leita.domain.problem

import jakarta.persistence.*

@Embeddable
open class Description(

    @Column(name = "description_problem", columnDefinition = "TEXT")
    val problem: String,

    @Column(name = "description_input", columnDefinition = "TEXT")
    val input: String,

    @Column(name = "description_output", columnDefinition = "TEXT")
    val output: String,
)