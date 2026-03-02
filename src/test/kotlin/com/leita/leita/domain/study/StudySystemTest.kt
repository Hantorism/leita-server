package com.leita.leita.domain.study

import com.leita.leita.common.security.SecurityRole
import com.leita.leita.domain.user.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("스터디 시스템 종합 테스트")
class StudySystemTest {

    private fun createUser(email: String, name: String): User {
        return User(
            name = name,
            email = email,
            profileImage = null,
            githubInfo = null,
            sub = email,
            role = SecurityRole.USER
        ).apply {
            this.id = email.hashCode().toLong().coerceAtLeast(1)
        }
    }

    @Test
    @DisplayName("전체 스터디 플로우 - 생성부터 세션 관리까지")
    fun testFullStudyFlow() {
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
            admin = admin,
            attendanceCheckRequired = true,
            assignmentRequired = true,
            requiredAttendanceCount = 10,
            requiredAssignmentCount = 5
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
            startDateTime = now.plusDays(1).withHour(19).withMinute(0),
            endDateTime = now.plusDays(1).withHour(21).withMinute(0),
            studyId = 1L
        )

        println("   ✅ 세션 생성 완료")
        println("   ✅ 시작: ${session.startDateTime}")
        println("   ✅ 종료: ${session.endDateTime}")

        // 5. 출석 체크 시작
        println("\n5️⃣ 출석 체크 시작")
        val attendanceCheck = session.openAttendanceCheck(
            openTime = now.plusDays(1).withHour(19).withMinute(0),
            closeTime = now.plusDays(1).withHour(21).withMinute(0),
            lateThresholdMinutes = 10
        )

        // 모든 활성 멤버 등록
        study.getAllActiveMembers().forEach { member ->
            attendanceCheck.registerMember(member)
        }

        assertEquals(3, attendanceCheck.attendances.size)
        println("   ✅ 출석 체크 오픈")
        println("   ✅ 지각 기준: 시작 후 10분")
        println("   ✅ 등록된 멤버: ${attendanceCheck.attendances.size}명")

        // 6. 출석 응답
        println("\n6️⃣ 출석 응답")
        val attendTime1 = now.plusDays(1).withHour(19).withMinute(5)  // 5분 후 - PRESENT
        val attendTime2 = now.plusDays(1).withHour(19).withMinute(15) // 15분 후 - LATE

        attendanceCheck.attend(admin, attendTime1)
        attendanceCheck.attend(member1, attendTime2)
        // member2는 응답 안 함 - ABSENT

        val adminAttendance = attendanceCheck.attendances.find { it.user.id == admin.id }
        val member1Attendance = attendanceCheck.attendances.find { it.user.id == member1.id }
        val member2Attendance = attendanceCheck.attendances.find { it.user.id == member2.id }

        assertEquals(AttendanceStatus.PRESENT, adminAttendance?.status)
        assertEquals(AttendanceStatus.LATE, member1Attendance?.status)
        assertEquals(AttendanceStatus.ABSENT, member2Attendance?.status)

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

        // 9. 출석 체크 종료
        println("\n9️⃣ 출석 체크 종료")
        attendanceCheck.close(now.plusDays(1).withHour(21).withMinute(0))

        assertEquals(AttendanceCheckStatus.CLOSED, attendanceCheck.status)
        println("   ✅ 출석 체크 종료")
        println("   ✅ 상태: ${attendanceCheck.status}")

        // 최종 결과
        println("\n🎯 최종 결과")
        println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        println("📚 스터디 정보")
        println("   - 제목: ${study.title}")
        println("   - 관리자: ${study.getAdminUsers().size}명")
        println("   - 일반 멤버: ${study.getRegularUsers().size}명")
        println("   - 총 활성 멤버: ${study.getAllActiveMembers().size}명")

        println("\n📅 세션 정보")
        println("   - 출석 체크: ${session.attendanceChecks.size}개")
        println("   - 과제: ${session.assignments.size}개")

        println("\n✅ 출석 현황")
        attendanceCheck.attendances.forEach { att ->
            println("   - ${att.user.name}: ${att.status}")
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
        println("\n=== 수료 조건 검증 테스트 ===\n")

        val admin = createUser("admin@test.com", "관리자")

        // 출석만 필수
        println("1️⃣ 출석만 필수")
        val study1 = Study.create(
            title = "출석 중심 스터디",
            description = "출석이 중요",
            admin = admin,
            attendanceCheckRequired = true,
            assignmentRequired = false,
            requiredAttendanceCount = 10,
            requiredAssignmentCount = 0
        )
        assertTrue(study1.attendanceCheckRequired)
        assertFalse(study1.assignmentRequired)
        println("   ✅ 출석 ${study1.requiredAttendanceCount}회 이상 필수")

        // 과제만 필수
        println("\n2️⃣ 과제만 필수")
        val study2 = Study.create(
            title = "과제 중심 스터디",
            description = "과제가 중요",
            admin = admin,
            attendanceCheckRequired = false,
            assignmentRequired = true,
            requiredAttendanceCount = 0,
            requiredAssignmentCount = 5
        )
        assertFalse(study2.attendanceCheckRequired)
        assertTrue(study2.assignmentRequired)
        println("   ✅ 과제 ${study2.requiredAssignmentCount}개 이상 필수")

        // 둘 다 필수
        println("\n3️⃣ 출석 + 과제 모두 필수")
        val study3 = Study.create(
            title = "엄격한 스터디",
            description = "출석과 과제 모두 중요",
            admin = admin,
            attendanceCheckRequired = true,
            assignmentRequired = true,
            requiredAttendanceCount = 8,
            requiredAssignmentCount = 4
        )
        assertTrue(study3.attendanceCheckRequired)
        assertTrue(study3.assignmentRequired)
        println("   ✅ 출석 ${study3.requiredAttendanceCount}회 + 과제 ${study3.requiredAssignmentCount}개 이상 필수")

        println("\n✅ 수료 조건 검증 완료\n")
    }

    @Test
    @DisplayName("중복 방지 및 예외 처리")
    fun testDuplicatePreventionAndExceptions() {
        println("\n=== 중복 방지 및 예외 처리 테스트 ===\n")

        val admin = createUser("admin@test.com", "관리자")
        val member = createUser("member@test.com", "멤버")

        val study = Study.create(
            title = "테스트 스터디",
            description = "예외 테스트",
            admin = admin,
            attendanceCheckRequired = true,
            assignmentRequired = true,
            requiredAttendanceCount = 1,
            requiredAssignmentCount = 1
        )

        val now = LocalDateTime.now()
        val session = StudySession.create(
            startDateTime = now,
            endDateTime = now.plusHours(2),
            studyId = 1L
        )

        // 1. 중복 출석 체크 방지
        println("1️⃣ 중복 출석 체크 방지")
        session.openAttendanceCheck(now, now.plusHours(2), 10)
        val exception1 = assertThrows(Exception::class.java) {
            session.openAttendanceCheck(now, now.plusHours(2), 10)
        }
        assertNotNull(exception1)
        println("   ✅ 이미 진행 중인 출석 체크가 있을 때 새로 생성 불가")

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

