package com.leita.leita.domain.study

import com.leita.leita.domain.User
import com.leita.leita.repository.BaseEntity
import jakarta.persistence.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

@Entity
@Table(name = "study_class")
@Access(AccessType.FIELD)
open class StudyClass(

    @Column(nullable = false, unique = true)
    open var title: String,

    @Column(nullable = false)
    open var description: String,

    @Column(nullable = false)
    open var requirement : String,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "study_class_admins",
        joinColumns = [JoinColumn(name = "study_class_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    open var admins: MutableList<User> = mutableListOf(),

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "study_class_members",
        joinColumns = [JoinColumn(name = "study_class_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    open var members: MutableList<User> = mutableListOf(),

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "study_pendings",
        joinColumns = [JoinColumn(name = "study_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    open var pendings: MutableList<User> = mutableListOf()

) : BaseEntity() {

    companion object {
        fun create(
            title: String,
            description: String,
            requirement: String,
            admin: User
        ): StudyClass {
            require(title.isNotBlank()) { "스터디 제목은 필수입니다." }
            require(description.isNotBlank()) { "스터디 설명은 필수입니다." }

            val study = StudyClass(
                title = title,
                description = description,
                requirement = requirement
            )
            study.admins.add(admin)
            study.members.add(admin)
            return study
        }
    }

    fun update(title: String, description: String, requirement: String) {
        this.title = title
        this.description = description
        this.requirement = requirement
    }

    fun join(user: User) {
        pendings.add(user)
    }

    fun approve(user: User) {
        pendings.remove(user)
        members.add(user)
    }

    fun deny(user: User) {
        pendings.remove(user)
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

    fun isMemberByEmail(email: String): Boolean {
        return admins.any { it.email == email }
    }

    fun changeRole(user: User, newRole: StudyRole): Boolean {
        val roleMap = mapOf(
            StudyRole.ADMIN to Pair(admins, members),
            StudyRole.MEMBER to Pair(members, admins)
        )

        roleMap[newRole]?.let { (addTo, removeFrom) ->
            addTo.add(user)
            if (removeFrom.contains(user)) {
                removeFrom.remove(user)
            }
            return true
        }
        return false
    }

    fun getPendingsPage(pageable: Pageable): Page<User> {
        val sortedPendings = pendings.sortedByDescending { it.createdAt }
        val start = pageable.offset.toInt()
        val end = minOf(start + pageable.pageSize, sortedPendings.size)
        val content = if (start < end) sortedPendings.subList(start, end) else emptyList()
        return PageImpl(content, pageable, sortedPendings.size.toLong())
    }
}