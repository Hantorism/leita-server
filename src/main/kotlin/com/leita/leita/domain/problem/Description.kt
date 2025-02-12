package com.leita.leita.domain.problem

import jakarta.persistence.*

@Embeddable
data class Description(

    @Column(name = "description_problem")
    val problem: String,

    @Column(name = "description_input")
    val input: String,

    @Column(name = "description_output")
    val output: String,
)