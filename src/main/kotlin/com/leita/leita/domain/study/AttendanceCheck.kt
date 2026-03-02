package com.leita.leita.domain.study

import com.leita.leita.common.exception.CustomException
import com.leita.leita.domain.BaseEntity
import com.leita.leita.domain.user.User
import jakarta.persistence.*
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

@Entity
@Table(name = "attendance_check")
@Access(AccessType.FIELD)
open class AttendanceCheck(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_session_id", nullable = false)
    open val studySession: StudySession,

    @Column(nullable = false)
    open val openTime: LocalDateTime,

    @Column(nullable = false)
    open var closeTime: LocalDateTime,

    @Column(nullable = false)
    open val lateThresholdMinutes: Int = 10,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    open var status: AttendanceCheckStatus = AttendanceCheckStatus.OPEN,

    @OneToMany(mappedBy = "attendanceCheck", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    open val attendances: MutableList<Attendance> = mutableListOf()
) : BaseEntity() {

    companion object {
        fun create(
            studySession: StudySession,
            openTime: LocalDateTime,
            closeTime: LocalDateTime,
            lateThresholdMinutes: Int = 10
        ): AttendanceCheck {
            if (!closeTime.isAfter(openTime)) {
                throw CustomException("출석 체크 종료 시간은 시작 시간보다 늦어야 합니다.", HttpStatus.BAD_REQUEST)
            }
            if (lateThresholdMinutes <= 0) {
                throw CustomException("지각 기준 시간은 1분 이상이어야 합니다.", HttpStatus.BAD_REQUEST)
            }

            return AttendanceCheck(
                studySession = studySession,
                openTime = openTime,
                closeTime = closeTime,
                lateThresholdMinutes = lateThresholdMinutes,
                status = AttendanceCheckStatus.OPEN
            )
        }
    }

    fun registerMember(user: User) {
        if (attendances.any { it.user.id == user.id }) return
        attendances.add(
            Attendance(
                attendanceCheck = this,
                user = user,
                status = AttendanceStatus.ABSENT
            )
        )
    }

    fun attend(user: User, attendedAt: LocalDateTime = LocalDateTime.now()): Attendance {
        if (status == AttendanceCheckStatus.CLOSED) {
            throw CustomException("이미 종료된 출석 체크입니다.", HttpStatus.BAD_REQUEST)
        }
        if (attendedAt.isBefore(openTime)) {
            throw CustomException("출석 체크 시작 전입니다.", HttpStatus.BAD_REQUEST)
        }
        if (attendedAt.isAfter(closeTime)) {
            throw CustomException("출석 체크 시간이 종료되었습니다.", HttpStatus.BAD_REQUEST)
        }

        val deadline = openTime.plusMinutes(lateThresholdMinutes.toLong())
        val attendanceStatus = if (attendedAt.isAfter(deadline)) AttendanceStatus.LATE else AttendanceStatus.PRESENT

        val attendance = attendances.find { it.user.id == user.id }
            ?: Attendance(
                attendanceCheck = this,
                user = user,
                status = AttendanceStatus.ABSENT
            ).also { attendances.add(it) }

        attendance.status = attendanceStatus
        attendance.attendedAt = attendedAt
        return attendance
    }

    fun close(closedAt: LocalDateTime = LocalDateTime.now()) {
        if (status == AttendanceCheckStatus.CLOSED) {
            throw CustomException("이미 종료된 출석 체크입니다.", HttpStatus.BAD_REQUEST)
        }
        if (closedAt.isBefore(openTime)) {
            throw CustomException("출석 체크 시작 전에는 종료할 수 없습니다.", HttpStatus.BAD_REQUEST)
        }
        status = AttendanceCheckStatus.CLOSED
        closeTime = closedAt
    }
}

@Entity
@Table(name = "attendance")
@Access(AccessType.FIELD)
open class Attendance(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_check_id", nullable = false)
    open val attendanceCheck: AttendanceCheck,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    open val user: User,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    open var status: AttendanceStatus,

    @Column(nullable = true)
    open var attendedAt: LocalDateTime? = null
) : BaseEntity()
