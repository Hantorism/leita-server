package com.leita.leita.study.domain

import com.leita.leita.user.domain.User
import com.leita.leita.common.domain.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime


@Entity
@Table(
    name = "study_member",
    uniqueConstraints = [UniqueConstraint(columnNames = ["study_id", "user_id"])]
)
@Access(AccessType.FIELD)
open class StudyMember(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    open val study: Study,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    open val user: User,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    open var role: StudyMemberRole,

    @Column(nullable = false)
    open val joinedAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = true)
    open var approvedAt: LocalDateTime? = null
) : BaseEntity() {

    companion object {
        fun create(study: Study, user: User, role: StudyMemberRole): StudyMember {
            val approvedAt = if (role == StudyMemberRole.MEMBER || role == StudyMemberRole.ADMIN) {
                LocalDateTime.now()
            } else {
                null
            }

            return StudyMember(
                study = study,
                user = user,
                role = role,
                approvedAt = approvedAt
            )
        }
    }

    fun changeRole(newRole: StudyMemberRole) {
        this.role = newRole

        // MEMBER나 ADMIN으로 변경 시 승인 시간 기록
        if ((newRole == StudyMemberRole.MEMBER || newRole == StudyMemberRole.ADMIN) && this.approvedAt == null) {
            this.approvedAt = LocalDateTime.now()
        }
    }
}

