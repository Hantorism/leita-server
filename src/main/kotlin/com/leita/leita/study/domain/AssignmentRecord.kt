package com.leita.leita.study.domain

import com.leita.leita.common.domain.BaseEntity
import com.leita.leita.user.domain.User
import jakarta.persistence.*

@Entity
@Table(name = "assignment_record", uniqueConstraints = [UniqueConstraint(columnNames = ["assignment_id", "user_id"])])
@Access(AccessType.FIELD)
open class AssignmentRecord(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    open val assignment: Assignment,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    open val user: User,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    open var status: AssignmentStatus = AssignmentStatus.INCOMPLETE
) : BaseEntity() {

    fun updateStatus(newStatus: AssignmentStatus) {
        this.status = newStatus
    }
}
