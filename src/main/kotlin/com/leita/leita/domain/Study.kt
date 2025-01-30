package com.leita.leita.domain

import com.leita.leita.repository.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "study")
class Study(

    @ManyToMany
    @JoinTable(
        name = "study_admins",
        joinColumns = [JoinColumn(name = "study_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    val admins: MutableList<User> = mutableListOf(),

    @Column(nullable = false, unique = true)
    val title: String,

    @Column(nullable = false)
    val description: String,

    @ManyToMany
    @JoinTable(
        name = "study_participants",
        joinColumns = [JoinColumn(name = "study_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    val participants: MutableList<User> = mutableListOf()
) : BaseEntity() {

    fun join(user: User) {
        participants.add(user)
    }

    fun leave(user: User) {
        participants.remove(user)
    }

    fun addAdmin(user: User) {
        admins.add(user)
    }

    fun removeAdmin(user: User) {
        admins.remove(user)
    }
}