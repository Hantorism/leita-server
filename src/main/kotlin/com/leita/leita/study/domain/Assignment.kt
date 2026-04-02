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

    @Column(nullable = false)
    open var title: String,

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
            title: String,
            description: String?,
            problemIds: List<Long>
        ): Assignment {
            validate(title, problemIds)

            val assignment = Assignment(
                studySession = studySession,
                title = title,
                description = description
            )
            assignment.problemIds.addAll(problemIds.distinct())
            return assignment
        }

        internal fun validate(title: String, problemIds: List<Long>) {
            if (title.isBlank()) {
                throw CustomException("과제 제목은 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST)
            }
            if (problemIds.isEmpty()) {
                throw CustomException("과제 문제는 최소 1개 이상 필요합니다.", HttpStatus.BAD_REQUEST)
            }
        }
    }

    fun update(title: String, description: String?, problemIds: List<Long>) {
        validate(title, problemIds)

        this.title = title
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


