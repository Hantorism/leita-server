package com.leita.leita.study.domain

import com.leita.leita.common.exception.CustomException
import com.leita.leita.common.domain.BaseEntity
import jakarta.persistence.*
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

@Entity
@Table(name = "study_session")
@Access(AccessType.FIELD)
open class StudySession(
    @Column(nullable = false)
    open var startDateTime: LocalDateTime,

    @Column(nullable = false)
    open var endDateTime: LocalDateTime,

    @Column(nullable = false)
    open val studyId: Long,

    @OneToMany(mappedBy = "studySession", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    open val attendances: MutableSet<Attendance> = mutableSetOf(),

    @OneToMany(mappedBy = "studySession", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    open val assignments: MutableSet<Assignment> = mutableSetOf()
) : BaseEntity() {

    companion object {
        fun create(
            startDateTime: LocalDateTime,
            endDateTime: LocalDateTime,
            studyId: Long
        ): StudySession {
            validateDateTimeRange(startDateTime, endDateTime)
            return StudySession(
                startDateTime = startDateTime,
                endDateTime = endDateTime,
                studyId = studyId
            )
        }

        internal fun validateDateTimeRange(startDateTime: LocalDateTime, endDateTime: LocalDateTime) {
            if (!endDateTime.isAfter(startDateTime)) {
                throw CustomException("세션 종료 시간은 시작 시간보다 늦어야 합니다.", HttpStatus.BAD_REQUEST)
            }
        }
    }

    fun update(startDateTime: LocalDateTime, endDateTime: LocalDateTime) {
        validateDateTimeRange(startDateTime, endDateTime)
        this.startDateTime = startDateTime
        this.endDateTime = endDateTime
    }

    fun openAttendance(
        openTime: LocalDateTime,
        closeTime: LocalDateTime,
        lateThresholdMinutes: Int
    ): Attendance {
        if (attendances.any { it.status == AttendanceStatus.OPEN }) {
            throw CustomException("이미 진행 중인 출석이 있습니다.", HttpStatus.BAD_REQUEST)
        }

        val attendance = Attendance.create(
            studySession = this,
            openTime = openTime,
            closeTime = closeTime,
            lateThresholdMinutes = lateThresholdMinutes
        )
        attendances.add(attendance)
        return attendance
    }

    fun createAssignment(
        title: String,
        description: String?,
        problemIds: List<Long>
    ): Assignment {
        if (assignments.isNotEmpty()) {
            throw CustomException("이미 과제가 존재합니다.", HttpStatus.BAD_REQUEST)
        }

        val assignment = Assignment.create(
            studySession = this,
            title = title,
            description = description,
            problemIds = problemIds
        )
        assignments.add(assignment)
        return assignment
    }

    fun getAssignment(): Assignment? = assignments.firstOrNull()
}
