package com.leita.leita.domain.study

import com.leita.leita.common.exception.CustomException
import com.leita.leita.domain.BaseEntity
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
    open val attendanceChecks: MutableList<AttendanceCheck> = mutableListOf(),

    @OneToMany(mappedBy = "studySession", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    open val assignments: MutableList<Assignment> = mutableListOf()
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
        Companion.validateDateTimeRange(startDateTime, endDateTime)
        this.startDateTime = startDateTime
        this.endDateTime = endDateTime
    }

    fun openAttendanceCheck(
        openTime: LocalDateTime,
        closeTime: LocalDateTime,
        lateThresholdMinutes: Int = 10
    ): AttendanceCheck {
        if (attendanceChecks.any { it.status == AttendanceCheckStatus.OPEN }) {
            throw CustomException("이미 진행 중인 출석 체크가 있습니다.", HttpStatus.BAD_REQUEST)
        }

        val attendanceCheck = AttendanceCheck.create(
            studySession = this,
            openTime = openTime,
            closeTime = closeTime,
            lateThresholdMinutes = lateThresholdMinutes
        )
        attendanceChecks.add(attendanceCheck)
        return attendanceCheck
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
