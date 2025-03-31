package com.leita.leita.domain.problem

import jakarta.persistence.*

@Embeddable
open class Solved(

    @Column(name = "solved_count")
    var count: Long = 0,

    @Column(name = "solved_rate")
    var rate: Double = 0.0,
) {
    fun updateSolved(isSolved: Boolean) {
        if (isSolved) {
            this.count++
        } else if (this.count > 0) {
            this.count--
        }

        this.rate = if (this.count > 0) {
            (this.rate * (this.count - 1) + (if (isSolved) 1 else 0)) / this.count
        } else {
            0.0
        }
    }
}