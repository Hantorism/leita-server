package com.leita.leita.domain.problem

import com.leita.leita.domain.User
import com.leita.leita.repository.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "problem")
class Problem(

    @Column(nullable = false)
    val title: String,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val author: User,

    @Column(nullable = false)
    val description: Description,

    @Column(nullable = false)
    val limit: Limit,

    @OneToMany(mappedBy = "problem", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var testCases: List<TestCase> = mutableListOf(),

    @Column
    val source: String,

    @Column(nullable = false)
    val solved: Solved,

    @ElementCollection
    @CollectionTable(name = "problem_category", joinColumns = [JoinColumn(name = "problem_id")])
    @Column
    val category: List<String>,

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