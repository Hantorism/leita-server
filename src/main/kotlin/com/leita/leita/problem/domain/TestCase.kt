package com.leita.leita.problem.domain

import com.leita.leita.common.domain.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "problem_test_cases")
@Access(AccessType.FIELD)
open class TestCase(

    @Column(nullable = false, columnDefinition = "TEXT")
    open var input: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    open var output: String,

    @Column(nullable = false)
    open var isShow: Boolean

) : BaseEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    open var problem: Problem? = null
        protected set

    fun withProblem(problem: Problem): TestCase {
        this.problem = problem
        return this
    }

    fun show() {
        this.isShow = true
    }

    fun hide() {
        this.isShow = false
    }
}