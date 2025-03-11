package com.leita.leita.domain.problem

import com.leita.leita.domain.User
import com.leita.leita.repository.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "problem")
@Access(AccessType.FIELD)
open class Problem(

    @Column(nullable = false)
    open val title: String,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    open val author: User,

    @Column(nullable = false)
    open val description: Description,

    @Column(nullable = false)
    open val limit: Limit,

    @OneToMany(mappedBy = "problem", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    open val testCases: MutableList<TestCase> = mutableListOf(),

    @Column
    open val source: String,

    @Column(nullable = false)
    open val solved: Solved,

    @ElementCollection
    @CollectionTable(name = "problem_category", joinColumns = [JoinColumn(name = "problem_id")])
    @Column
    open val category: List<String>,

) : BaseEntity() {
    fun addTestCases(testCases: List<TestCase>): Problem {
        this.testCases += testCases
        return this
    }

    fun update(id: Long): Problem {
        this.id = id
        return this
    }
}