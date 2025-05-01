package com.leita.leita.domain.study

import com.leita.leita.domain.User
import com.leita.leita.repository.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "study")
@Access(AccessType.FIELD)
open class Study(

    @Column(nullable = false, unique = true)
    open val title: String,

    @Column(nullable = false)
    open val description: String,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "study_members",
        joinColumns = [JoinColumn(name = "study_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    open val members: MutableList<User> = mutableListOf(),

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "study_pendings",
        joinColumns = [JoinColumn(name = "study_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    open val pending: MutableList<User> = mutableListOf()

) : BaseEntity() {

    fun join(user: User) {
        pending.add(user)
    }

    fun approve(user: User) {
        pending.remove(user)
        members.add(user)
    }

    fun deny(user: User) {
        pending.remove(user)
    }

    fun leave(user: User) {
        members.remove(user)
    }
}