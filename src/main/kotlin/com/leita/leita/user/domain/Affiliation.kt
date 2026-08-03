package com.leita.leita.user.domain

import com.leita.leita.common.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "affiliations")
class Affiliation(
    @Column(nullable = false, unique = true)
    var name: String,

    @Column(nullable = false, unique = true)
    var emailDomain: String
) : BaseEntity()
