package com.leita.leita.judge.event

data class ProblemJudgedEvent(
    val userId: Long,
    val problemId: Long
)
