package com.leita.leita.domain.problem

import com.leita.leita.repository.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "problems")
class Problem(

    @Column(nullable = false)
    val memoryLimit: Long,

    @Column(nullable = false)
    val timeLimit: Long,

//    TODO: 문제 형식(사진 유무)에 따라 타입 변경 필요
    @Column(nullable = false)
    val problem: String,

    @OneToMany(mappedBy = "problem", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val testCases: List<TestCase> = mutableListOf()
) : BaseEntity()