package com.leita.leita.qna.domain

import com.leita.leita.common.domain.BaseEntity
import com.leita.leita.user.domain.User
import jakarta.persistence.Access
import jakarta.persistence.AccessType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "qnas")
@Access(AccessType.FIELD)
open class Qna(
    @Column(nullable = false)
    open var title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    open var content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    open var author: User,

    @Column(nullable = true, columnDefinition = "TEXT")
    open var answer: String? = null,

    @Column(nullable = true)
    open var answeredAt: LocalDateTime? = null
) : BaseEntity() {
    fun update(title: String, content: String) {
        this.title = title
        this.content = content
    }

    fun reply(answer: String) {
        this.answer = answer
        this.answeredAt = LocalDateTime.now()
    }
}
