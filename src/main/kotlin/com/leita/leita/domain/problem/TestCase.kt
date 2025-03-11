package com.leita.leita.domain.problem

import com.fasterxml.jackson.annotation.JsonIgnore
import com.leita.leita.repository.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "problem_test_cases")
@Access(AccessType.FIELD)
open class TestCase(

    @Column(nullable = false)
    open val input: String,

    @Column(nullable = false)
    open val output: String,

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "problem_id", nullable = false)
    open var problem: Problem? = null
): BaseEntity() {
    fun createTestCase(problem: Problem): TestCase {
        this.problem = problem
        return this
    }
}