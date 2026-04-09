package com.leita.leita.study

import com.leita.leita.common.security.SecurityRole
import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.judge.domain.*
import com.leita.leita.judge.repository.JudgeRepository
import com.leita.leita.study.domain.*
import com.leita.leita.study.repository.StudyMemberRepository
import com.leita.leita.study.repository.StudyRepository
import com.leita.leita.study.repository.StudySessionRepository
import com.leita.leita.study.service.StudyService
import com.leita.leita.user.domain.User
import com.leita.leita.user.repository.UserRepository
import com.leita.leita.util.mail.MailUtil
import com.leita.leita.problem.repository.ProblemRepository
import com.leita.leita.problem.domain.Problem
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.ArgumentMatchers.anyList
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Optional

@DisplayName("StudyMemberStatus 조회 테스트")
class StudyMemberStatusTest {

    private lateinit var studyRepository: StudyRepository
    private lateinit var studyMemberRepository: StudyMemberRepository
    private lateinit var studySessionRepository: StudySessionRepository
    private lateinit var judgeRepository: JudgeRepository
    private lateinit var problemRepository: ProblemRepository
    private lateinit var userRepository: UserRepository
    private lateinit var jwtUtils: JwtUtils
    private lateinit var mailUtil: MailUtil
    private lateinit var studyService: StudyService

    private lateinit var adminUser: User
    private lateinit var memberUser: User
    private lateinit var study: Study
    private lateinit var session: StudySession

    @BeforeEach
    fun setUp() {
        studyRepository = mock(StudyRepository::class.java)
        studyMemberRepository = mock(StudyMemberRepository::class.java)
        studySessionRepository = mock(StudySessionRepository::class.java)
        judgeRepository = mock(JudgeRepository::class.java)
        problemRepository = mock(ProblemRepository::class.java)
        userRepository = mock(UserRepository::class.java)
        jwtUtils = mock(JwtUtils::class.java)
        mailUtil = mock(MailUtil::class.java)

        studyService = StudyService(
            studyRepository,
            studyMemberRepository,
            studySessionRepository,
            judgeRepository,
            problemRepository,
            userRepository,
            jwtUtils,
            mailUtil
        )

        adminUser = createUser(1L, "admin@test.com", "관리자")
        memberUser = createUser(2L, "member@test.com", "멤버")

        study = Study.create(
            title = "테스트 스터디",
            description = "설명",
            requirement = "조건",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusMonths(1),
            admin = adminUser
        ).apply { id = 100L }
        study.join(memberUser)
        study.approve(memberUser)

        session = StudySession.create(
            title = "1주차 세션",
            description = "세션 설명",
            startDateTime = LocalDateTime.now().minusDays(1),
            endDateTime = LocalDateTime.now().minusDays(1).plusHours(2),
            studyId = 100L
        ).apply { id = 200L }
    }

    @Test
    fun `멤버의 전체 현황을 조회할 수 있다`() {
        // given
        val baseTime = LocalDateTime.of(2026, 4, 2, 10, 0)
        `when`(studyRepository.findDetailById(100L)).thenReturn(study)
        `when`(userRepository.findById(memberUser.id)).thenReturn(Optional.of(memberUser))
        `when`(jwtUtils.extractEmail()).thenReturn(memberUser.email)
        `when`(studySessionRepository.findAllByStudyIdOrderByStartDateTimeDesc(100L)).thenReturn(listOf(session))
        
        // 출석 기록 추가
        val attendance = session.openAttendance(baseTime, baseTime.plusHours(1), 10)
        attendance.registerMember(memberUser)
        attendance.attend(memberUser, baseTime.plusMinutes(5)) // PRESENT

        // 과제 추가
        val assignment = session.createAssignment("설명", listOf(1001L, 1002L), LocalDateTime.now(), LocalDateTime.now().plusDays(7))
        assignment.records.add(AssignmentRecord(assignment, memberUser, AssignmentStatus.PARTIAL))
        
        // 과제 해결 여부 (1001번만 해결)
        `when`(judgeRepository.findByProblemIdInAndUserIdAndResult(listOf(1001L, 1002L), memberUser.id, Result.CORRECT))
            .thenReturn(listOf(createJudge(1001L, memberUser)))

        // when
        val response = studyService.getMemberStatus(100L, null, memberUser.id)

        // then
        assertThat(response).hasSize(1)
        val status = response[0]
        assertThat(status.user.id).isEqualTo(memberUser.id)
        assertThat(status.sessions).hasSize(1)
        assertThat(status.sessions[0].attendanceStatus).isEqualTo(AttendanceRecordStatus.PRESENT)
        assertThat(status.sessions[0].assignmentStatus).isEqualTo(AssignmentStatus.PARTIAL) // 하나만 풀었으므로 PARTIAL
    }

    @Test
    fun `멤버의 과제 상세 현황을 조회할 수 있다`() {
        // given
        `when`(studyRepository.findDetailById(100L)).thenReturn(study)
        `when`(userRepository.findById(memberUser.id)).thenReturn(Optional.of(memberUser))
        `when`(jwtUtils.extractEmail()).thenReturn(adminUser.email)
        `when`(studySessionRepository.findAllByStudyIdOrderByStartDateTimeDesc(100L)).thenReturn(listOf(session))
        
        val assignment = session.createAssignment("설명", listOf(1001L, 1002L), LocalDateTime.now(), LocalDateTime.now().plusDays(7))
        assignment.records.add(AssignmentRecord(assignment, memberUser, AssignmentStatus.COMPLETED))
        
        // Mock problems
        val p1 = mock(Problem::class.java).apply { `when`(id).thenReturn(1001L); `when`(title).thenReturn("P1") }
        val p2 = mock(Problem::class.java).apply { `when`(id).thenReturn(1002L); `when`(title).thenReturn("P2") }
        `when`(problemRepository.findAllById(anyList())).thenReturn(listOf(p1, p2))

        // 1001, 1002 모두 해결
        val judge1 = createJudge(1001L, memberUser)
        val judge2 = createJudge(1002L, memberUser)
        `when`(judgeRepository.findByProblemIdInAndUserIdAndType(listOf(1001L, 1002L), memberUser.id, JudgeType.SUBMIT))
            .thenReturn(listOf(judge1, judge2))

        // when
        val response = studyService.getMemberAssignment(100L, null, memberUser.id)

        // then
        assertThat(response).hasSize(1)
        val assignmentDetail = response[0].assignments[0]
        assertThat(assignmentDetail.solvedCount).isEqualTo(2)
        assertThat(assignmentDetail.totalCount).isEqualTo(2)
        assertThat(assignmentDetail.status).isEqualTo(AssignmentStatus.COMPLETED)
        assertThat(assignmentDetail.problems).hasSize(2)
        assertThat(assignmentDetail.problems.map { it.problemId }).containsExactlyInAnyOrder(1001L, 1002L)
        assertThat(assignmentDetail.problems.map { it.result }).allMatch { it == Result.CORRECT }
    }

    private fun createUser(id: Long, email: String, name: String): User {
        return User(name, email, null, null, email, SecurityRole.USER).apply { this.id = id }
    }

    private fun createJudge(problemId: Long, user: User): Judge {
        return Judge(
            problemId = problemId,
            user = user,
            result = Result.CORRECT,
            used = UsedInfo(0, 0, Language.PYTHON),
            type = JudgeType.SUBMIT
        )
    }
}
