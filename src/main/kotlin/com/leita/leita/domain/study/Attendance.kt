package com.leita.leita.domain.study

import com.leita.leita.common.exception.CustomException
import com.leita.leita.domain.BaseEntity
import com.leita.leita.domain.user.User
import jakarta.persistence.*
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

@Entity
@Table(name = "attendance")
@Access(AccessType.FIELD)
open class Attendance(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_session_id", nullable = false)
    open val studySession: StudySession,

    @Column(nullable = false)
    open val openTime: LocalDateTime,

    @Column(nullable = false)
    open var closeTime: LocalDateTime,

    @Column(nullable = false)
    open val lateThresholdMinutes: Int = 0,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    open var status: AttendanceStatus = AttendanceStatus.OPEN,

    @OneToMany(mappedBy = "attendance", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    open val records: MutableSet<AttendanceRecord> = mutableSetOf()
) : BaseEntity() {

    companion object {
        fun create(
            studySession: StudySession,
            openTime: LocalDateTime,
            closeTime: LocalDateTime,
            lateThresholdMinutes: Int = 0
        ): Attendance {
            if (!closeTime.isAfter(openTime)) {
                throw CustomException("출석 종료 시간은 시작 시간보다 늦어야 합니다.", HttpStatus.BAD_REQUEST)
            }

            return Attendance(
                studySession = studySession,
                openTime = openTime,
                closeTime = closeTime,
                lateThresholdMinutes = lateThresholdMinutes,
                status = AttendanceStatus.OPEN
            )
        }
    }

    fun registerMember(user: User) {
        if (records.any { it.user.id == user.id }) return
        records.add(
            AttendanceRecord(
                attendance = this,
                user = user,
                status = AttendanceRecordStatus.ABSENT
            )
        )
    }

    fun attend(user: User, attendedAt: LocalDateTime = LocalDateTime.now()): AttendanceRecord {
        if (status == AttendanceStatus.CLOSED) {
            throw CustomException("이미 종료된 출석입니다.", HttpStatus.BAD_REQUEST)
        }
        if (attendedAt.isBefore(openTime)) {
            throw CustomException("출석 시작 전입니다.", HttpStatus.BAD_REQUEST)
        }
        if (attendedAt.isAfter(closeTime)) {
            throw CustomException("출석 시간이 종료되었습니다.", HttpStatus.BAD_REQUEST)
        }

        val deadline = openTime.plusMinutes(lateThresholdMinutes.toLong())
        val recordStatus = if (attendedAt.isAfter(deadline)) AttendanceRecordStatus.LATE else AttendanceRecordStatus.PRESENT

        val record = records.find { it.user.id == user.id }
            ?: AttendanceRecord(
                attendance = this,
                user = user,
                status = AttendanceRecordStatus.ABSENT
            ).also { records.add(it) }

        record.status = recordStatus
        record.attendedAt = attendedAt
        return record
    }

    fun close(closedAt: LocalDateTime = LocalDateTime.now()) {
        if (status == AttendanceStatus.CLOSED) {
            throw CustomException("이미 종료된 출석입니다.", HttpStatus.BAD_REQUEST)
        }
        if (closedAt.isBefore(openTime)) {
            throw CustomException("출석 시작 전에는 종료할 수 없습니다.", HttpStatus.BAD_REQUEST)
        }
        status = AttendanceStatus.CLOSED
        closeTime = closedAt
    }
}

@Entity
@Table(name = "attendance_record")
@Access(AccessType.FIELD)
open class AttendanceRecord(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id", nullable = false)
    open val attendance: Attendance,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    open val user: User,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    open var status: AttendanceRecordStatus,

    @Column(nullable = true)
    open var attendedAt: LocalDateTime? = null
) : BaseEntity()
