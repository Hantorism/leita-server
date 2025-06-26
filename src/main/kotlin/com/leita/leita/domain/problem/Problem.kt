package com.leita.leita.domain.problem

import com.leita.leita.domain.User
import com.leita.leita.repository.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "problem")
@Access(AccessType.FIELD)
open class Problem(

    @Column(nullable = false)
    open var title: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    open val author: User,

    @Column(nullable = false)
    open var description: Description,

    @Column(nullable = false)
    open var limit: Limit,

    @OneToMany(mappedBy = "problem", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    private val _testCases: MutableList<TestCase> = mutableListOf(),

    @Column
    open var source: String,

    @Column(nullable = false)
    open val solved: Solved,

    @ElementCollection
    @CollectionTable(name = "problem_category", joinColumns = [JoinColumn(name = "problem_id")])
    @Column(name = "category")
    private val _category: MutableList<String> = mutableListOf(),

    ) : BaseEntity() {

    val testCases: List<TestCase> get() = _testCases
    val category: List<String> get() = _category

    companion object {
        fun create(
            title: String,
            author: User,
            description: Description,
            limit: Limit,
            testCases: List<TestCase>,
            source: String,
            category: List<String>
        ): Problem {
            require(testCases.size >= 5) { "테스트 케이스는 최소 5개 이상이어야 합니다." }
            val solved = Solved(0, 0, 0.0)
            val problem = Problem(title, author, description, limit, mutableListOf(), source, solved, mutableListOf())
            problem._testCases.addAll(testCases.map { it.withProblem(problem) })
            problem._category.addAll(category)
            return problem
        }
    }

    fun update(
        title: String,
        description: Description,
        limit: Limit,
        testCases: List<TestCase>,
        source: String,
        category: List<String>
    ) {
        this.title = title
        this.description = description
        this.limit = limit
        this.source = source

        _testCases.clear()
        _testCases.addAll(testCases.map { it.withProblem(this) })

        _category.clear()
        _category.addAll(category)
    }

    fun addTestCases(testCases: List<TestCase>): Problem {
        _testCases.addAll(testCases.map { it.withProblem(this) })
        return this
    }
}