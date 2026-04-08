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
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Optional

@DisplayName("StudyMemberStatus 조회 테스트")
class StudyMemberStatusTest {

    private lateinit var studyRepository: StudyRepository
    private lateinit var studyMemberRepository: StudyMemberRepository
    private lateinit var studySessionRepository: StudySessionRepository
    private lateinit var judgeRepository: JudgeRepository
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
        userRepository = mock(UserRepository::class.java)
        jwtUtils = mock(JwtUtils::class.java)
        mailUtil = mock(MailUtil::class.java)

        studyService = StudyService(
            studyRepository,
            studyMemberRepository,
            studySessionRepository,
            judgeRepository,
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
        val assignment = session.createAssignment("설명", listOf(1001L, 1002L))
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
        assertThat(status.sessions[0].attendanceStatus).isEqualTo(AttendanceRecordStatus.PRESENT.name)
        assertThat(status.sessions[0].assignmentStatus).isEqualTo(AssignmentStatus.PARTIAL.name) // 하나만 풀었으므로 PARTIAL
    }

    @Test
    fun `멤버의 과제 상세 현황을 조회할 수 있다`() {
        // given
        `when`(studyRepository.findDetailById(100L)).thenReturn(study)
        `when`(userRepository.findById(memberUser.id)).thenReturn(Optional.of(memberUser))
        `when`(jwtUtils.extractEmail()).thenReturn(adminUser.email)
        `when`(studySessionRepository.findAllByStudyIdOrderByStartDateTimeDesc(100L)).thenReturn(listOf(session))
        
        val assignment = session.createAssignment("설명", listOf(1001L, 1002L))
        assignment.records.add(AssignmentRecord(assignment, memberUser, AssignmentStatus.COMPLETED))
        
        // 1001, 1002 모두 해결
        `when`(judgeRepository.findByProblemIdInAndUserIdAndResult(listOf(1001L, 1002L), memberUser.id, Result.CORRECT))
            .thenReturn(listOf(createJudge(1001L, memberUser), createJudge(1002L, memberUser)))

        // when
        val response = studyService.getMemberAssignment(100L, null, memberUser.id)

        // then
        assertThat(response).hasSize(1)
        val assignmentDetail = response[0].assignments[0]
        assertThat(assignmentDetail.solvedCount).isEqualTo(2)
        assertThat(assignmentDetail.totalCount).isEqualTo(2)
        assertThat(assignmentDetail.status).isEqualTo(AssignmentStatus.COMPLETED.name)
        assertThat(assignmentDetail.solvedProblemIds).containsExactlyInAnyOrder(1001L, 1002L)
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
