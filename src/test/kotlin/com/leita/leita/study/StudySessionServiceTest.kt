package com.leita.leita.study

import com.leita.leita.common.security.SecurityRole
import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.study.dto.AttendanceOpenRequest
import com.leita.leita.study.dto.MemberAttendanceUpdateRequest
import com.leita.leita.study.dto.StudySessionCreateRequest
import com.leita.leita.study.domain.*
import com.leita.leita.study.service.StudySessionService
import com.leita.leita.user.domain.User
import com.leita.leita.study.repository.StudyRepository
import com.leita.leita.study.repository.StudySessionRepository
import com.leita.leita.study.repository.StudyMemberRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.time.LocalDate
import java.time.LocalDateTime

@DisplayName("StudySessionService 테스트")
class StudySessionServiceTest {

    private lateinit var studyRepository: StudyRepository
    private lateinit var studySessionRepository: StudySessionRepository
    private lateinit var studyMemberRepository: StudyMemberRepository
    private lateinit var jwtUtils: JwtUtils
    private lateinit var studySessionService: StudySessionService

    private lateinit var adminUser: User
    private lateinit var memberUser: User
    private lateinit var study: Study

    @BeforeEach
    fun setUp() {
        studyRepository = mock(StudyRepository::class.java)
        studySessionRepository = mock(StudySessionRepository::class.java)
        studyMemberRepository = mock(StudyMemberRepository::class.java)
        jwtUtils = mock(JwtUtils::class.java)
        studySessionService = StudySessionService(
            studyRepository,
            studySessionRepository,
            studyMemberRepository,
            jwtUtils
        )

        adminUser = createUser(1L, "admin@test.com", "관리자")
        memberUser = createUser(2L, "member@test.com", "멤버")

        study = Study.create(
            title = "알고리즘 스터디",
            description = "코딩 테스트 대비",
            requirement = "열심히 할 사람",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusMonths(3),
            admin = adminUser,
        )
        study.join(memberUser)
        study.approve(memberUser)
    }

    @Test
    fun `관리자는 스터디 세션을 생성할 수 있다`() {
        `when`(studyRepository.findDetailById(1L)).thenReturn(study)
        `when`(jwtUtils.extractEmail()).thenReturn(adminUser.email)
        `when`(studySessionRepository.save(any(StudySession::class.java))).thenAnswer { invocation ->
            (invocation.getArgument(0) as StudySession).apply { id = 10L }
        }

        val response = studySessionService.createStudySession(
            1L,
            StudySessionCreateRequest(
                studyId = 1L,
                title = "1주차 세션",
                description = "기초 알고리즘",
                startDateTime = LocalDateTime.of(2026, 3, 20, 19, 0),
                endDateTime = LocalDateTime.of(2026, 3, 20, 21, 0)
            )
        )

        assertThat(response.id).isEqualTo(10L)
        assertThat(response.studyId).isEqualTo(1L)
        assertThat(response.title).isEqualTo("1주차 세션")
        assertThat(response.description).isEqualTo("기초 알고리즘")
        assertThat(response.assignment).isNull()
        assertThat(response.attendance).isNull()
    }

    @Test
    fun `관리자가 출석을 열면 활성 멤버들이 기본 등록된다`() {
        val session = StudySession.create(
            title = "세션 제목",
            description = "세션 설명",
            startDateTime = LocalDateTime.of(2026, 3, 20, 19, 0),
            endDateTime = LocalDateTime.of(2026, 3, 20, 21, 0),
            studyId = 1L
        ).apply { id = 20L }

        `when`(studyRepository.findDetailById(1L)).thenReturn(study)
        `when`(studySessionRepository.findDetailById(20L)).thenReturn(session)
        `when`(jwtUtils.extractEmail()).thenReturn(adminUser.email)

        val response = studySessionService.openAttendance(
            20L,
            AttendanceOpenRequest(
                lateThresholdMinutes = 15,
                openTime = LocalDateTime.of(2026, 3, 20, 19, 0),
                closeTime = LocalDateTime.of(2026, 3, 20, 19, 30)
            )
        )

        assertThat(response.records).hasSize(2)
        assertThat(response.records.map { it.userEmail })
            .containsExactlyInAnyOrder(adminUser.email, memberUser.email)
        assertThat(response.records.map { it.status }.distinct())
            .containsExactly(AttendanceRecordStatus.ABSENT.name)
    }

    @Test
    fun `멤버는 열린 출석에 출석할 수 있다`() {
        val session = StudySession.create(
            title = "세션 제목",
            description = "세션 설명",
            startDateTime = LocalDateTime.now().minusHours(1),
            endDateTime = LocalDateTime.now().plusHours(1),
            studyId = 1L
        ).apply { id = 30L }
        val attendance = session.openAttendance(
            openTime = LocalDateTime.now().minusMinutes(10),
            closeTime = LocalDateTime.now().plusMinutes(20),
            lateThresholdMinutes = 15
        )
        attendance.registerMember(adminUser)
        attendance.registerMember(memberUser)

        `when`(studyRepository.findDetailById(1L)).thenReturn(study)
        `when`(studySessionRepository.findDetailById(30L)).thenReturn(session)
        `when`(jwtUtils.extractUser()).thenReturn(memberUser)

        val response = studySessionService.attend(30L)
        val memberRecord = response.records.first { it.userId == memberUser.id }

        assertThat(memberRecord.status).isEqualTo(AttendanceRecordStatus.PRESENT.name)
        assertThat(memberRecord.attendedAt).isNotNull()
    }

    @Test
    fun `관리자는 특정 멤버의 출석 상태를 변경할 수 있다`() {
        val session = StudySession.create(
            title = "세션 제목",
            description = "세션 설명",
            startDateTime = LocalDateTime.now().minusHours(1),
            endDateTime = LocalDateTime.now().plusHours(1),
            studyId = 1L
        ).apply { id = 40L }
        val attendance = session.openAttendance(
            openTime = LocalDateTime.now().minusMinutes(30),
            closeTime = LocalDateTime.now().plusMinutes(30),
            lateThresholdMinutes = 15
        )
        attendance.registerMember(memberUser)

        val studyMember = StudyMember.create(study, memberUser, StudyMemberRole.MEMBER).apply { id = 100L }

        `when`(studySessionRepository.findDetailById(40L)).thenReturn(session)
        `when`(studyRepository.findDetailById(1L)).thenReturn(study)
        `when`(jwtUtils.extractEmail()).thenReturn(adminUser.email)
        `when`(studyMemberRepository.findById(100L)).thenReturn(java.util.Optional.of(studyMember))

        val response = studySessionService.updateMemberAttendanceStatus(
            40L,
            100L,
            MemberAttendanceUpdateRequest(AttendanceRecordStatus.LATE)
        )

        val memberRecord = response.records.first { it.userId == memberUser.id }
        assertThat(memberRecord.status).isEqualTo(AttendanceRecordStatus.LATE.name)
    }

    private fun createUser(id: Long, email: String, name: String): User {
        return User(
            name = name,
            email = email,
            profileImage = null,
            githubInfo = null,
            sub = email,
            role = SecurityRole.USER
        ).apply {
            this.id = id
        }
    }
}
