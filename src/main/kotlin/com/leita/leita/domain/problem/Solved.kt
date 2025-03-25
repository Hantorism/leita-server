package com.leita.leita.domain.problem

import jakarta.persistence.*

@Embeddable
open class Solved(

    @Column(name = "solved_count")
    var count: Long,

    @Column(name = "solved_rate")
    var rate: Double,
) {
    fun updateSolved(isSolved: Boolean) {
        if (isSolved) {
            this.count++
            this.rate = (this.rate * count) / count
        } else {
            this.rate = (this.rate * (count - 1)) / count
        }
    }
}