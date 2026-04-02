package com.leita.leita.study

import com.leita.leita.common.security.SecurityRole
import com.leita.leita.user.domain.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class StudySessionDomainTest {

    private fun user(id: Long, email: String): User {
        return User(
            name = "u$id",
            email = email,
            profileImage = null,
            githubInfo = null,
            sub = email,
            role = SecurityRole.USER,
        ).also { it.id = id }
    }

    @Test
    fun `open attendance check and attend`() {
        val now = LocalDateTime.now()
        val session = StudySession.create("Title", "Description", now.minusHours(1), now.plusHours(2), 1L)
        val attendance = session.openAttendance(now.minusMinutes(5), now.plusMinutes(30), 10)

        val member = user(11L, "m1@ajou.ac.kr")
        attendance.registerMember(member)
        val record = attendance.attend(member, now)

        assertEquals(AttendanceRecordStatus.PRESENT, record.status)
        assertEquals(1, session.attendances.size)
    }

    @Test
    fun `cannot open multiple attendance checks at same time`() {
        val now = LocalDateTime.now()
        val session = StudySession.create("Title", "Description", now.minusHours(1), now.plusHours(2), 1L)
        session.openAttendance(now.minusMinutes(5), now.plusMinutes(30), 10)

        assertThrows(RuntimeException::class.java) {
            session.openAttendance(now.minusMinutes(1), now.plusMinutes(40), 10)
        }
    }

    @Test
    fun `create assignment under session and prevent duplicate`() {
        val now = LocalDateTime.now()
        val session = StudySession.create("Title", "Description", now.minusHours(1), now.plusHours(2), 1L)

        val first = session.createAssignment(
            title = "assignment 1",
            description = null,
            problemIds = listOf(10001L, 10002L)
        )

        assertEquals(2, first.problemIds.size)
        assertEquals(1, session.assignments.size)

        assertThrows(RuntimeException::class.java) {
            session.createAssignment(
                title = "duplicate assignment",
                description = null,
                problemIds = listOf(10003L)
            )
        }
    }
}
