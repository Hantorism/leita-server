package com.leita.leita.domain.study

import com.leita.leita.common.exception.CustomException
import com.leita.leita.domain.BaseEntity
import com.leita.leita.domain.user.User
import jakarta.persistence.*
import org.springframework.http.HttpStatus

@Entity
@Table(name = "study")
@Access(AccessType.FIELD)
open class Study(

    @Column(nullable = false, unique = true)
    open var title: String,

    @Column(nullable = false)
    open var description: String,

    @Column(nullable = false)
    open var attendanceCheckRequired: Boolean = false,

    @Column(nullable = false)
    open var assignmentRequired: Boolean = false,

    @Column(nullable = false)
    open var requiredAttendanceCount: Int = 0,

    @Column(nullable = false)
    open var requiredAssignmentCount: Int = 0,

    @OneToMany(mappedBy = "study", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    open val studyMembers: MutableList<StudyMember> = mutableListOf()

) : BaseEntity() {

    companion object {
        fun create(
            title: String,
            description: String,
            admin: User,
            attendanceCheckRequired: Boolean = false,
            assignmentRequired: Boolean = false,
            requiredAttendanceCount: Int = 0,
            requiredAssignmentCount: Int = 0
        ): Study {
            if (description.isBlank()) {
                throw CustomException("스터디 설명은 필수입니다.", HttpStatus.BAD_REQUEST)
            }
            validateCompletionCondition(
                attendanceCheckRequired,
                assignmentRequired,
                requiredAttendanceCount,
                requiredAssignmentCount
            )

            val study = Study(
                title = title,
                description = description,
                attendanceCheckRequired = attendanceCheckRequired,
                assignmentRequired = assignmentRequired,
                requiredAttendanceCount = requiredAttendanceCount,
                requiredAssignmentCount = requiredAssignmentCount
            )
            study.addMember(admin, StudyMemberRole.ADMIN)
            return study
        }

        private fun validateCompletionCondition(
            attendanceCheckRequired: Boolean,
            assignmentRequired: Boolean,
            requiredAttendanceCount: Int,
            requiredAssignmentCount: Int
        ) {
            if (!attendanceCheckRequired && !assignmentRequired) {
                throw CustomException("수료 조건은 최소 1개 이상 필요합니다.", HttpStatus.BAD_REQUEST)
            }
            if (attendanceCheckRequired && requiredAttendanceCount <= 0) {
                throw CustomException("출석 수료 조건은 1 이상이어야 합니다.", HttpStatus.BAD_REQUEST)
            }
            if (assignmentRequired && requiredAssignmentCount <= 0) {
                throw CustomException("과제 수료 조건은 1 이상이어야 합니다.", HttpStatus.BAD_REQUEST)
            }
        }
    }

    // 편의 메서드 - 역할별 조회
    fun getAdmins(): List<User> = studyMembers
        .filter { it.role == StudyMemberRole.ADMIN }
        .map { it.user }

    fun getAdminUsers(): List<User> = getAdmins()

    fun getMembers(): List<User> = studyMembers
        .filter { it.role == StudyMemberRole.MEMBER }
        .map { it.user }

    fun getRegularUsers(): List<User> = getMembers()

    fun getPendings(): List<User> = studyMembers
        .filter { it.role == StudyMemberRole.PENDING }
        .map { it.user }

    fun getPendingUsers(): List<User> = getPendings()

    fun getAllActiveMembers(): List<User> = studyMembers
        .filter { it.role in listOf(StudyMemberRole.ADMIN, StudyMemberRole.MEMBER) }
        .map { it.user }

    // 멤버 관리
    fun addMember(user: User, role: StudyMemberRole) {
        val existing = studyMembers.find { it.user.id == user.id }
        if (existing != null) {
            throw CustomException("User is already a member", HttpStatus.BAD_REQUEST)
        }

        val studyMember = StudyMember.create(this, user, role)
        studyMembers.add(studyMember)
    }

    fun join(user: User) {
        addMember(user, StudyMemberRole.PENDING)
    }

    fun approve(user: User) {
        val studyMember = studyMembers.find { it.user.id == user.id }
            ?: throw CustomException("User is not in pending list", HttpStatus.BAD_REQUEST)

        if (studyMember.role != StudyMemberRole.PENDING) {
            throw CustomException("User is not pending", HttpStatus.BAD_REQUEST)
        }

        studyMember.changeRole(StudyMemberRole.MEMBER)
    }

    fun deny(user: User) {
        val studyMember = studyMembers.find { it.user.id == user.id }
            ?: throw CustomException("User is not in pending list", HttpStatus.BAD_REQUEST)

        if (studyMember.role != StudyMemberRole.PENDING) {
            throw CustomException("User is not pending", HttpStatus.BAD_REQUEST)
        }

        studyMembers.remove(studyMember)
    }

    fun leave(user: User) {
        val studyMember = studyMembers.find { it.user.id == user.id }
            ?: throw CustomException("User is not a member of the study", HttpStatus.BAD_REQUEST)

        if (studyMember.role == StudyMemberRole.ADMIN && getAdmins().size == 1) {
            throw CustomException("Cannot leave - last admin", HttpStatus.BAD_REQUEST)
        }

        studyMembers.remove(studyMember)
    }

    fun changeRole(user: User, newRole: StudyMemberRole) {
        if (newRole == StudyMemberRole.PENDING) {
            throw CustomException("Cannot change to PENDING role", HttpStatus.BAD_REQUEST)
        }

        val studyMember = studyMembers.find { it.user.id == user.id }
            ?: throw CustomException("User is not a member", HttpStatus.BAD_REQUEST)

        if (studyMember.role == StudyMemberRole.PENDING) {
            throw CustomException("Cannot change pending member role", HttpStatus.BAD_REQUEST)
        }

        if (studyMember.role == StudyMemberRole.ADMIN && newRole == StudyMemberRole.MEMBER && getAdmins().size == 1) {
            throw CustomException("Cannot demote last admin", HttpStatus.BAD_REQUEST)
        }

        studyMember.changeRole(newRole)
    }

    // 권한 확인
    fun checkAdminByEmail(email: String) {
        val isAdmin = studyMembers.any { it.user.email == email && it.role == StudyMemberRole.ADMIN }
        if (!isAdmin) {
            throw CustomException("Permission denied", HttpStatus.FORBIDDEN)
        }
    }

    fun checkMemberByEmail(email: String) {
        val isMember = studyMembers.any {
            it.user.email == email && it.role in listOf(StudyMemberRole.ADMIN, StudyMemberRole.MEMBER)
        }
        if (!isMember) {
            throw CustomException("Permission denied", HttpStatus.FORBIDDEN)
        }
    }

    fun update(
        title: String,
        description: String,
        attendanceCheckRequired: Boolean,
        assignmentRequired: Boolean,
        requiredAttendanceCount: Int,
        requiredAssignmentCount: Int
    ) {
        if (description.isBlank()) {
            throw CustomException("스터디 설명은 필수입니다.", HttpStatus.BAD_REQUEST)
        }
        Companion.validateCompletionCondition(
            attendanceCheckRequired,
            assignmentRequired,
            requiredAttendanceCount,
            requiredAssignmentCount
        )

        this.title = title
        this.description = description
        this.attendanceCheckRequired = attendanceCheckRequired
        this.assignmentRequired = assignmentRequired
        this.requiredAttendanceCount = requiredAttendanceCount
        this.requiredAssignmentCount = requiredAssignmentCount
    }
}
