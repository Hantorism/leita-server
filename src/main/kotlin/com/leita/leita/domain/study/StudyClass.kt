package com.leita.leita.domain.study

import com.leita.leita.common.exception.CustomException
import com.leita.leita.domain.User
import com.leita.leita.repository.BaseEntity
import jakarta.persistence.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus

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
        name = "study_class_pendings",
        joinColumns = [JoinColumn(name = "study_class_id")],
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
            if( requirement.isBlank() || description.isBlank() ) {
                throw CustomException("스터디 요건은 필수입니다.", HttpStatus.BAD_REQUEST)
            }

            val study = StudyClass(
                title = title,
                description = description,
                requirement = requirement
            )
            study.admins.add(admin)
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
        if( !pendings.contains(user) ) {
            throw CustomException("User is not in pending list", HttpStatus.BAD_REQUEST)
        }
        pendings.remove(user)
        members.add(user)
    }

    fun deny(user: User) {
        if( !pendings.contains(user) ) {
            throw CustomException("User is not in pending list", HttpStatus.BAD_REQUEST)
        }
        pendings.remove(user)
    }

    fun leave(user: User) {
        if( !members.contains(user) ) {
            throw CustomException("User is not a member of the study", HttpStatus.BAD_REQUEST)
        }
        members.remove(user)
    }

    fun addAdmin(user: User) {
        admins.add(user)
    }

    fun removeAdmin(user: User) {
        if( admins.contains(user) ) {
            throw CustomException("User is not an admin of the study", HttpStatus.BAD_REQUEST)
        }
        admins.remove(user)
    }

    fun isAdminByEmail(email: String): Boolean {
        return admins.any { it.email == email }
    }

    fun isMemberByEmail(email: String): Boolean {
        return admins.any { it.email == email }
    }

    fun changeToRoleAdmin(user: User) {
        if( !members.contains(user) ) {
            throw CustomException("User is not a member of the study", HttpStatus.BAD_REQUEST)
        }
        if( admins.contains(user) ) {
            throw CustomException("User is already an admin", HttpStatus.BAD_REQUEST)
        }
        members.remove(user)
        admins.add(user)
    }

    fun changeToRoleMember(user: User) {
        if( !admins.contains(user) ) {
            throw CustomException("User is not an admin of the study", HttpStatus.BAD_REQUEST)
        }
        if( members.contains(user) ) {
            throw CustomException("User is already an member", HttpStatus.BAD_REQUEST)
        }
        admins.remove(user)
        members.add(user)
    }

    fun getPendingsPage(pageable: Pageable): Page<User> {
        val sortedPendings = pendings.sortedByDescending { it.createdAt }
        val start = pageable.offset.toInt()
        val end = minOf(start + pageable.pageSize, sortedPendings.size)
        val content = if (start < end) sortedPendings.subList(start, end) else emptyList()
        return PageImpl(content, pageable, sortedPendings.size.toLong())
    }
}