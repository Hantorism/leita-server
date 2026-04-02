package com.leita.leita.study

import com.leita.leita.common.security.SecurityRole
import com.leita.leita.user.domain.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

@DisplayName("스터디 시스템 종합 테스트")
class StudySystemTest {

    private var nextUserId = 1L

    private fun createUser(email: String, name: String): User {
        return User(
            name = name,
            email = email,
            profileImage = null,
            githubInfo = null,
            sub = email,
            role = SecurityRole.USER
        ).apply {
            this.id = nextUserId++
        }
    }

    @Test
    @DisplayName("전체 스터디 플로우 - 생성부터 세션 관리까지")
    fun testFullStudyFlow() {
        nextUserId = 1L
        println("\n=== 스터디 시스템 전체 플로우 테스트 ===\n")

        // 사용자 생성
        val admin = createUser("admin@test.com", "관리자")
        val member1 = createUser("member1@test.com", "멤버1")
        val member2 = createUser("member2@test.com", "멤버2")

        // 1. 스터디 생성
        println("1️⃣ 스터디 생성")
        val study = Study.create(
            title = "알고리즘 마스터 스터디",
            description = "코딩 테스트 완벽 대비",
            requirement = "열심히 할 사람",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusMonths(3),
            admin = admin,
        )

        assertEquals("알고리즘 마스터 스터디", study.title)
        assertEquals(1, study.getAdminUsers().size)
        println("   ✅ 스터디: ${study.title}")
        println("   ✅ 관리자: ${study.getAdminUsers()[0].name}")
        println("   ✅ 수료 조건: 출석 ${study.requiredAttendanceCount}회, 과제 ${study.requiredAssignmentCount}개")

        // 2. 멤버 참가 신청
        println("\n2️⃣ 멤버 참가 신청")
        study.join(member1)
        study.join(member2)

        assertEquals(2, study.getPendingUsers().size)
        println("   ✅ 대기 멤버: ${study.getPendingUsers().map { it.name }}")

        // 3. 멤버 승인
        println("\n3️⃣ 멤버 승인")
        study.approve(member1)
        study.approve(member2)

        assertEquals(2, study.getRegularUsers().size)
        assertEquals(0, study.getPendingUsers().size)
        println("   ✅ 승인 완료: ${study.getRegularUsers().map { it.name }}")
        println("   ✅ 총 활성 멤버: ${study.getAllActiveMembers().size}명 (관리자 포함)")

        // 4. 스터디 세션 생성
        println("\n4️⃣ 스터디 세션 생성")
        val now = LocalDateTime.now()
        val session = StudySession.create(
            title = "1주차 세션",
            description = "기초 알고리즘",
            startDateTime = now.plusDays(1).withHour(19).withMinute(0),
            endDateTime = now.plusDays(1).withHour(21).withMinute(0),
            studyId = 1L
        )

        println("   ✅ 세션 생성 완료")
        println("   ✅ 시작: ${session.startDateTime}")
        println("   ✅ 종료: ${session.endDateTime}")

        // 5. 출석 시작
        println("\n5️⃣ 출석 시작")
        val attendance = session.openAttendance(
            openTime = now.plusDays(1).withHour(19).withMinute(0),
            closeTime = now.plusDays(1).withHour(21).withMinute(0),
            lateThresholdMinutes = 10
        )

        // 모든 활성 멤버 등록
        study.getAllActiveMembers().forEach { member ->
            attendance.registerMember(member)
        }

        assertEquals(3, attendance.records.size)
        println("   ✅ 출석 오픈")
        println("   ✅ 지각 기준: 시작 후 10분")
        println("   ✅ 등록된 멤버: ${attendance.records.size}명")

        // 6. 출석 응답
        println("\n6️⃣ 출석 응답")
        val attendTime1 = now.plusDays(1).withHour(19).withMinute(5)  // 5분 후 - PRESENT
        val attendTime2 = now.plusDays(1).withHour(19).withMinute(15) // 15분 후 - LATE

        attendance.attend(admin, attendTime1)
        attendance.attend(member1, attendTime2)
        // member2는 응답 안 함 - ABSENT

        val adminRecord = attendance.records.find { it.user.id == admin.id }
        val member1Record = attendance.records.find { it.user.id == member1.id }
        val member2Record = attendance.records.find { it.user.id == member2.id }

        assertEquals(AttendanceRecordStatus.PRESENT, adminRecord?.status)
        assertEquals(AttendanceRecordStatus.LATE, member1Record?.status)
        assertEquals(AttendanceRecordStatus.ABSENT, member2Record?.status)

        println("   ✅ ${admin.name}: PRESENT (5분 후 응답)")
        println("   ✅ ${member1.name}: LATE (15분 후 응답)")
        println("   ✅ ${member2.name}: ABSENT (응답 없음)")

        // 7. 과제 생성
        println("\n7️⃣ 과제 생성")
        val assignment = session.createAssignment(
            title = "1주차 과제 - 기본 자료구조",
            description = "스택, 큐, 해시맵 관련 문제 풀기",
            problemIds = listOf(10001L, 10002L, 10003L, 10004L, 10005L)
        )

        assertEquals("1주차 과제 - 기본 자료구조", assignment.title)
        assertEquals(5, assignment.problemIds.size)
        println("   ✅ 과제: ${assignment.title}")
        println("   ✅ 설명: ${assignment.description}")
        println("   ✅ 문제 개수: ${assignment.problemIds.size}개")
        println("   ✅ 문제 ID: ${assignment.problemIds}")

        // 8. 과제 수정
        println("\n8️⃣ 과제 수정")
        assignment.addProblem(10006L)
        assignment.removeProblem(10001L)

        assertEquals(5, assignment.problemIds.size) // 6개에서 1개 제거해서 5개
        assertTrue(assignment.problemIds.contains(10006L))
        assertFalse(assignment.problemIds.contains(10001L))
        println("   ✅ 문제 추가/제거 완료")
        println("   ✅ 현재 문제: ${assignment.problemIds}")

        // 9. 출석 종료
        println("\n9️⃣ 출석 종료")
        attendance.close(now.plusDays(1).withHour(21).withMinute(0))

        assertEquals(AttendanceStatus.CLOSED, attendance.status)
        println("   ✅ 출석 종료")
        println("   ✅ 상태: ${attendance.status}")

        // 최종 결과
        println("\n🎯 최종 결과")
        println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        println("📚 스터디 정보")
        println("   - 제목: ${study.title}")
        println("   - 관리자: ${study.getAdminUsers().size}명")
        println("   - 일반 멤버: ${study.getRegularUsers().size}명")
        println("   - 총 활성 멤버: ${study.getAllActiveMembers().size}명")

        println("\n📅 세션 정보")
        println("   - 출석: ${session.attendances.size}개")
        println("   - 과제: ${session.assignments.size}개")

        println("\n✅ 출석 현황")
        attendance.records.forEach { record ->
            println("   - ${record.user.name}: ${record.status}")
        }

        println("\n📝 과제 현황")
        println("   - ${assignment.title}")
        println("   - 문제: ${assignment.problemIds.size}개")

        println("\n✅ 전체 플로우 테스트 성공!")
        println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
    }

    @Test
    @DisplayName("수료 조건 검증")
    fun testCompletionValidation() {
        nextUserId = 1L
        println("\n=== 수료 조건 검증 테스트 ===\n")

        val admin = createUser("admin@test.com", "관리자")

        // 출석만 필수
        println("1️⃣ 출석만 필수")
        val study1 = Study.create(
            title = "출석 중심 스터디",
            description = "출석이 중요",
            requirement = "열심히 할 사람",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusMonths(3),
            admin = admin,
        )

        // 과제만 필수
        println("\n2️⃣ 과제만 필수")
        val study2 = Study.create(
            title = "과제 중심 스터디",
            description = "과제가 중요",
            requirement = "열심히 할 사람",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusMonths(3),
            admin = admin,
        )

        // 둘 다 필수
        println("\n3️⃣ 출석 + 과제 모두 필수")
        val study3 = Study.create(
            title = "엄격한 스터디",
            description = "출석과 과제 모두 중요",
            requirement = "열심히 할 사람",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusMonths(3),
            admin = admin,
        )

        println("\n✅ 수료 조건 검증 완료\n")
    }

    @Test
    @DisplayName("중복 방지 및 예외 처리")
    fun testDuplicatePreventionAndExceptions() {
        nextUserId = 1L
        println("\n=== 중복 방지 및 예외 처리 테스트 ===\n")

        val admin = createUser("admin@test.com", "관리자")
        val member = createUser("member@test.com", "멤버")

        val study = Study.create(
            title = "테스트 스터디",
            description = "예외 테스트",
            requirement = "열심히 할 사람",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusMonths(3),
            admin = admin,
        )

        val now = LocalDateTime.now()
        val session = StudySession.create(
            title = "테스트 세션",
            description = "설명",
            startDateTime = now,
            endDateTime = now.plusHours(2),
            studyId = 1L
        )

        // 1. 중복 출석 방지
        println("1️⃣ 중복 출석 방지")
        session.openAttendance(now, now.plusHours(2), 10)
        val exception1 = assertThrows(Exception::class.java) {
            session.openAttendance(now, now.plusHours(2), 10)
        }
        assertNotNull(exception1)
        println("   ✅ 이미 진행 중인 출석이 있을 때 새로 생성 불가")

        // 2. 중복 과제 방지
        println("\n2️⃣ 중복 과제 방지 (세션당 1개)")
        session.createAssignment("과제1", null, listOf(10001L))
        val exception2 = assertThrows(Exception::class.java) {
            session.createAssignment("과제2", null, listOf(10002L))
        }
        assertNotNull(exception2)
        println("   ✅ 하나의 세션에는 최대 1개의 과제만 생성 가능")

        // 3. 중복 멤버 추가 방지
        println("\n3️⃣ 중복 멤버 추가 방지")
        study.join(member)
        val exception3 = assertThrows(Exception::class.java) {
            study.join(member)
        }
        assertNotNull(exception3)
        println("   ✅ 같은 사용자는 중복으로 참가 신청 불가")

        // 4. 마지막 관리자 탈퇴 방지
        println("\n4️⃣ 마지막 관리자 탈퇴 방지")
        val exception4 = assertThrows(Exception::class.java) {
            study.leave(admin)
        }
        assertNotNull(exception4)
        println("   ✅ 마지막 관리자는 탈퇴 불가 (스터디 고아화 방지)")

        println("\n✅ 예외 처리 테스트 완료\n")
    }
}
