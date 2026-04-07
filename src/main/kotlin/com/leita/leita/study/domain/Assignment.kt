package com.leita.leita.study.domain

import com.leita.leita.common.exception.CustomException
import com.leita.leita.common.domain.BaseEntity
import jakarta.persistence.*
import org.springframework.http.HttpStatus

@Entity
@Table(name = "assignment")
@Access(AccessType.FIELD)
open class Assignment(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_session_id", nullable = false)
    open val studySession: StudySession,

    @Column(nullable = true)
    open var description: String?,

    @ElementCollection
    @CollectionTable(name = "assignment_problem", joinColumns = [JoinColumn(name = "assignment_id")])
    @Column(name = "problem_id")
    open val problemIds: MutableList<Long> = mutableListOf()
) : BaseEntity() {

    companion object {
        fun create(
            studySession: StudySession,
            description: String?,
            problemIds: List<Long>
        ): Assignment {
            validate(problemIds)

            val assignment = Assignment(
                studySession = studySession,
                description = description
            )
            assignment.problemIds.addAll(problemIds.distinct())
            return assignment
        }

        internal fun validate(problemIds: List<Long>) {
            if (problemIds.isEmpty()) {
                throw CustomException("과제 문제는 최소 1개 이상 필요합니다.", HttpStatus.BAD_REQUEST)
            }
        }
    }

    fun update(description: String?, problemIds: List<Long>) {
        validate(problemIds)

        this.description = description
        this.problemIds.clear()
        this.problemIds.addAll(problemIds.distinct())
    }

    fun addProblem(problemId: Long) {
        if (problemIds.contains(problemId)) return
        problemIds.add(problemId)
    }

    fun removeProblem(problemId: Long) {
        problemIds.remove(problemId)
    }
}
