package com.leita.leita.study

import com.leita.leita.study.domain.Study
import com.leita.leita.study.domain.StudyMemberRole
import com.leita.leita.user.domain.User
import com.leita.leita.common.security.SecurityRole
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate

@DisplayName("스터디 전체 흐름 단위 테스트")
class StudyProcessTest {

    private lateinit var adminUser: User
    private lateinit var memberUser1: User
    private lateinit var memberUser2: User
    private lateinit var study: Study

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

    @BeforeEach
    fun setUp() {
        nextUserId = 1L
        adminUser = createUser("admin@test.com", "Admin")
        memberUser1 = createUser("member1@test.com", "Member1")
        memberUser2 = createUser("member2@test.com", "Member2")

        study = Study.create(
            title = "알고리즘 마스터",
            description = "코딩 테스트 준비",
            requirement = "열심히 할 사람",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusMonths(3),
            admin = adminUser,
        )
    }

    @Test
    @DisplayName("1. 스터디 생성")
    fun testCreateStudy() {
        println("✅ 스터디 생성: ${study.title}")
        assert(study.title == "알고리즘 마스터")
        assert(study.description == "코딩 테스트 준비")
        assert(study.getAdminUsers().size == 1)
        assert(study.getAdminUsers().contains(adminUser))
        println("   - 관리자: ${study.getAdminUsers().first().name}")
    }

    @Test
    @DisplayName("2. 멤버 신청 (PENDING)")
    fun testMemberJoin() {
        study.join(memberUser1)
        study.join(memberUser2)

        println("✅ 멤버 신청: 2명")
        assert(study.getPendingUsers().size == 2)
        println("   - 대기 중: ${study.getPendingUsers().map { it.name }}")
    }

    @Test
    @DisplayName("3. 멤버 승인 (PENDING -> MEMBER)")
    fun testApproveMember() {
        study.join(memberUser1)
        study.join(memberUser2)

        study.approve(memberUser1)

        println("✅ 멤버 승인: 1명")
        assert(study.getRegularUsers().size == 1)
        assert(study.getPendingUsers().size == 1)
        assert(study.getRegularUsers().contains(memberUser1))
        println("   - 일반 멤버: ${study.getRegularUsers().map { it.name }}")
        println("   - 대기 중: ${study.getPendingUsers().map { it.name }}")
    }

    @Test
    @DisplayName("4. 멤버 거부 (PENDING 제거)")
    fun testDenyMember() {
        study.join(memberUser1)
        study.join(memberUser2)

        study.deny(memberUser2)

        println("✅ 멤버 거부: 1명")
        assert(study.getPendingUsers().size == 1)
        assert(study.getPendingUsers().contains(memberUser1))
        println("   - 대기 중: ${study.getPendingUsers().map { it.name }}")
    }

    @Test
    @DisplayName("5. 역할 변경 (MEMBER -> ADMIN)")
    fun testChangeRole() {
        study.join(memberUser1)
        study.approve(memberUser1)

        assert(study.getRegularUsers().size == 1)
        assert(study.getAdminUsers().size == 1)

        study.changeRole(memberUser1, StudyMemberRole.ADMIN)

        println("✅ 역할 변경: MEMBER -> ADMIN")
        assert(study.getRegularUsers().size == 0)
        assert(study.getAdminUsers().size == 2)
        assert(study.getAdminUsers().contains(memberUser1))
        println("   - 관리자: ${study.getAdminUsers().map { it.name }}")
    }

    @Test
    @DisplayName("6. 멤버 탈퇴")
    fun testLeaveMember() {
        study.join(memberUser1)
        study.approve(memberUser1)

        assert(study.getRegularUsers().size == 1)

        study.leave(memberUser1)

        println("✅ 멤버 탈퇴: 1명")
        assert(study.getRegularUsers().size == 0)
        println("   - 일반 멤버: ${study.getRegularUsers().map { it.name }}")
    }

    @Test
    @DisplayName("7. 전체 CRUD 프로세스")
    fun testFullStudyProcess() {
        println("\n=== 스터디 전체 프로세스 테스트 ===\n")

        // 1. 스터디 생성
        println("1️⃣ 스터디 생성")
        assert(study.title == "알고리즘 마스터")
        assert(study.getAdminUsers().size == 1)
        println("   ✅ 스터디 생성 완료: ${study.title}")
        println("   ✅ 관리자 수: ${study.getAdminUsers().size}명\n")

        // 2. 멤버 신청
        println("2️⃣ 멤버 신청")
        study.join(memberUser1)
        study.join(memberUser2)
        assert(study.getPendingUsers().size == 2)
        println("   ✅ 신청 완료: ${study.getPendingUsers().map { it.name }}")
        println("   ✅ 대기 멤버: ${study.getPendingUsers().size}명\n")

        // 3. 첫 번째 멤버 승인
        println("3️⃣ 첫 번째 멤버 승인")
        study.approve(memberUser1)
        assert(study.getRegularUsers().size == 1)
        assert(study.getPendingUsers().size == 1)
        println("   ✅ 승인 완료: ${memberUser1.name}")
        println("   ✅ 일반 멤버: ${study.getRegularUsers().map { it.name }}")
        println("   ✅ 대기 중: ${study.getPendingUsers().map { it.name }}\n")

        // 4. 두 번째 멤버 거부
        println("4️⃣ 두 번째 멤버 거부")
        study.deny(memberUser2)
        assert(study.getPendingUsers().size == 0)
        println("   ✅ 거부 완료: ${memberUser2.name}")
        println("   ✅ 대기 멤버 남음: ${study.getPendingUsers().size}명\n")

        // 5. 멤버 역할 변경
        println("5️⃣ 멤버 역할 변경 (MEMBER -> ADMIN)")
        study.changeRole(memberUser1, StudyMemberRole.ADMIN)
        assert(study.getAdminUsers().size == 2)
        assert(study.getRegularUsers().size == 0)
        println("   ✅ 역할 변경 완료: ${memberUser1.name}")
        println("   ✅ 관리자 수: ${study.getAdminUsers().size}명 ${study.getAdminUsers().map { it.name }}")
        println("   ✅ 일반 멤버 수: ${study.getRegularUsers().size}명\n")

        // 6. 멤버 탈퇴
        println("6️⃣ 멤버 탈퇴")
        study.leave(memberUser1)
        assert(study.getAdminUsers().size == 1)
        println("   ✅ 탈퇴 완료: ${memberUser1.name}")
        println("   ✅ 관리자 수: ${study.getAdminUsers().size}명\n")

        // 7. 최종 상태
        println("7️⃣ 최종 스터디 상태")
        println("   📊 총 멤버: ${study.getAllActiveMembers().size}명")
        println("   👨‍💼 관리자: ${study.getAdminUsers().size}명 ${study.getAdminUsers().map { it.name }}")
        println("   👤 일반 멤버: ${study.getRegularUsers().size}명 ${study.getRegularUsers().map { it.name }}")
        println("   ⏳ 대기 중: ${study.getPendingUsers().size}명\n")

        println("✅ 전체 프로세스 성공!\n")
    }

    @Test
    @DisplayName("9. 권한 확인 - 관리자")
    fun testAdminPermission() {
        study.join(memberUser1)
        study.approve(memberUser1)

        study.checkAdminByEmail(adminUser.email)
        println("✅ 관리자 권한 확인: ${adminUser.email}")
    }

    @Test
    @DisplayName("10. 권한 확인 - 활성 멤버")
    fun testMemberPermission() {
        study.join(memberUser1)
        study.approve(memberUser1)

        study.checkMemberByEmail(memberUser1.email)
        println("✅ 멤버 권한 확인: ${memberUser1.email}")
    }
}
