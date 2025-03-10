package com.leita.leita.domain.problem

import com.fasterxml.jackson.annotation.JsonIgnore
import com.leita.leita.repository.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "problem_test_cases")
open class TestCase(

    @Column(nullable = false)
    val input: String,

    @Column(nullable = false)
    val output: String,

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "problem_id", nullable = false)
    var problem: Problem? = null
): BaseEntity() {
    fun createTestCase(problem: Problem): TestCase {
        this.problem = problem
        return this
    }
}