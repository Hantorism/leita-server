package com.leita.leita.domain.study

import com.leita.leita.domain.User
import com.leita.leita.repository.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "study")
@Access(AccessType.FIELD)
open class Study(
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "study_participants",
        joinColumns = [JoinColumn(name = "study_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    open val participants: MutableList<User> = mutableListOf(),

    @Column(nullable = false)
    open var startDateTime: LocalDateTime,

    @Column(nullable = false)
    open var endDateTime: LocalDateTime,

    @Column(nullable = false)
    open val studyClassId: Long,

    ) : BaseEntity() {

    companion object {
        fun create(
            startDateTime: LocalDateTime,
            endDateTime: LocalDateTime,
            studyClassId: Long
        ): Study {
            return Study(
                participants = mutableListOf(),
                startDateTime,
                endDateTime,
                studyClassId
            )
        }
    }

    fun attend(user: User) {
        participants.add(user)
    }

    fun update(startDateTime: LocalDateTime, endDateTime: LocalDateTime) {
        this.startDateTime = startDateTime
        this.endDateTime = endDateTime
    }
}