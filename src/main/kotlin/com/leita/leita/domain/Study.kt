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
        name = "study_members",
        joinColumns = [JoinColumn(name = "study_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    val members: MutableList<User> = mutableListOf(),

    @OneToMany
    @JoinTable(
        name = "study_pendings",
        joinColumns = [JoinColumn(name = "study_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    val pending: MutableList<User> = mutableListOf()

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

    fun addAdmin(user: User) {
        admins.add(user)
    }

    fun removeAdmin(user: User) {
        admins.remove(user)
    }

    fun isAdminByEmail(email: String): Boolean {
        return admins.any { it.email == email }
    }
}