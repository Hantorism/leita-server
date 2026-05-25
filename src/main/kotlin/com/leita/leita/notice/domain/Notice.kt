package com.leita.leita.notice.domain

import com.leita.leita.common.domain.BaseEntity
import jakarta.persistence.Access
import jakarta.persistence.AccessType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "notices")
@Access(AccessType.FIELD)
open class Notice(
    @Column(nullable = false)
    open var title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    open var content: String,

    @Column(nullable = false)
    open var authorName: String
) : BaseEntity() {
    fun update(title: String, content: String) {
        this.title = title
        this.content = content
    }
}
