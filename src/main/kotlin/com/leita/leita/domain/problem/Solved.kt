package com.leita.leita.domain.problem

import jakarta.persistence.*

@Embeddable
open class Solved(

    @Column(name = "solved_success_count")
    var successCount: Long = 0,

    @Column(name = "solved_total_count")
    var totalCount: Long = 0,

    @Column(name = "solved_rate")
    var rate: Double = 0.0,
) {
    fun updateSolved(isSolved: Boolean) {
        if (isSolved) {
            this.successCount++
        }
        this.totalCount++

        this.rate = (successCount.toDouble() / totalCount) * 100
    }
}