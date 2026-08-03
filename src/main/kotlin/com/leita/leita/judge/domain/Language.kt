package com.leita.leita.judge.domain

import com.leita.leita.common.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "languages")
class Language(
    @Column(nullable = false, unique = true)
    var name: String,

    @Column(nullable = false, unique = true)
    var code: String,

    @Column(nullable = false)
    var extension: String
) : BaseEntity() {
    fun getUrl(baseUrl: String): String {
        return baseUrl.replace("{LANGUAGE}", this.code)
    }

    fun toExtension(): String {
        return this.extension
    }
}