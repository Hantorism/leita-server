package com.leita.leita.domain.problem

import com.leita.leita.repository.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "test_cases")
class TestCase(

    @Column(nullable = false)
    val request: String,

    @Column(nullable = false)
    val response: String,

    @ManyToOne
    @JoinColumn(name = "problem_id", nullable = false)
    var problem: Problem? = null
): BaseEntity()