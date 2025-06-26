package com.leita.leita.domain.problem

import com.fasterxml.jackson.annotation.JsonIgnore
import com.leita.leita.repository.BaseEntity
import jakarta.persistence.*

@Entity
@Table(indexes = [Index(name = "idx_testcase_isshow", columnList = "is_show")], name = "problem_test_cases")
@Access(AccessType.FIELD)
open class TestCase(

    @Column(nullable = false, columnDefinition = "TEXT")
    open var input: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    open var output: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    open var isShow: Boolean

) : BaseEntity() {

    @JsonIgnore
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