package com.leita.leita.study.domain

import com.leita.leita.common.exception.CustomException
import com.leita.leita.common.domain.BaseEntity
import com.leita.leita.user.domain.User
import jakarta.persistence.*
import org.springframework.http.HttpStatus
import java.time.LocalDate

@Entity
@Table(name = "study")
@Access(AccessType.FIELD)
open class Study(

    @Column(nullable = false, unique = true)
    open var title: String,

    @Column(nullable = true)
    open var description: String,

    @Column(nullable = true)
    open var requirement: String?,

    @Column(nullable = true)
    open var startDate: LocalDate,

    @Column(nullable = true)
    open var endDate: LocalDate,

    @OneToMany(mappedBy = "study", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    open val studyMembers: MutableSet<StudyMember> = mutableSetOf()

) : BaseEntity() {

    companion object {
        fun create(
            title: String,
            description: String,
            requirement: String?,
            startDate: LocalDate,
            endDate: LocalDate,
            admin: User,
        ): Study {
            if (description.isBlank()) {
                throw CustomException("스터디 설명은 필수입니다.", HttpStatus.BAD_REQUEST)
            }
            validateDateTimeRange(startDate, endDate)

            val study = Study(
                title = title,
                description = description,
                requirement = requirement,
                startDate = startDate,
                endDate = endDate,
            )
            study.addMember(admin, StudyMemberRole.ADMIN)
            return study
        }

        private fun validateDateTimeRange(startDate: LocalDate, endDate: LocalDate) {
            if (!endDate.isAfter(startDate)) {
                throw CustomException("스터디 종료일은 시작일보다 늦어야 합니다.", HttpStatus.BAD_REQUEST)
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
        requirement: String?,
        startDate: LocalDate,
        endDate: LocalDate,
    ) {
        if (description.isBlank()) {
            throw CustomException("스터디 설명은 필수입니다.", HttpStatus.BAD_REQUEST)
        }
        validateDateTimeRange(startDate, endDate)

        this.title = title
        this.description = description
        this.requirement = requirement
        this.startDate = startDate
        this.endDate = endDate
    }
}
