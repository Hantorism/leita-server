package com.leita.leita.problem.domain

import com.leita.leita.common.exception.CustomException
import com.leita.leita.user.domain.User
import com.leita.leita.common.domain.BaseEntity
import jakarta.persistence.*
import org.springframework.http.HttpStatus
import kotlin.random.Random

@Entity
@Table(name = "problem")
@Access(AccessType.FIELD)
@NamedEntityGraph(
    name = "Problem.withTestCases",
    attributeNodes = [NamedAttributeNode("testCases")]
)
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

    @Column
    open var source: String,

    @Column(nullable = false)
    open val solved: Solved,

    @OneToMany(
        mappedBy = "problem",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    open var testCases: MutableList<TestCase> = mutableListOf(),

    @ElementCollection
    @CollectionTable(name = "problem_category", joinColumns = [JoinColumn(name = "problem_id")])
    @Column(name = "category")
    open var category: MutableList<String> = mutableListOf(),

    @Column(nullable = false, unique = true)
    open val problemId: String

) : BaseEntity() {

    companion object {
        fun create(
            title: String,
            author: User,
            description: Description,
            limit: Limit,
            testCases: List<TestCase>,
            source: String,
            category: List<String>,
            problemId: String = generateProblemId()
        ): Problem {
            if(testCases.size < 5) {
                throw CustomException("테스트 케이스는 최소 5개 이상이어야 합니다.", HttpStatus.BAD_REQUEST)
            }

            val problem = Problem(
                title = title,
                author = author,
                description = description,
                limit = limit,
                source = source,
                solved = Solved(0, 0, 0.0),
                problemId = problemId
            )

            testCases.forEach { it.withProblem(problem) }
            problem.testCases.addAll(testCases)
            problem.category.addAll(category)

            return problem
        }

        fun generateProblemId(): String {
            val randomVal = Random.nextInt(0, 0xFFFF)
            return String.format("%04x", randomVal)
        }

        fun generateProblemId(excludeProblemId: String): String {
            val maxAttempts = 10
            repeat(maxAttempts) {
                val problemId = generateProblemId()
                if(problemId != excludeProblemId) {
                    return problemId
                }
            }
            throw IllegalStateException("문제 ID 생성 실패: $maxAttempts 회 시도 중 중복 발생")
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

        this.testCases.clear()
        testCases.forEach { it.withProblem(this) }
        this.testCases.addAll(testCases)

        this.category.clear()
        this.category.addAll(category)
    }

    fun filterVisibleTestCases(): Problem {
        this.testCases = this.testCases.filter { it.isShow }.toMutableList()
        return this
    }
}