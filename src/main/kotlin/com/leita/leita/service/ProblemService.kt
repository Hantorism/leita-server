package com.leita.leita.service

import com.leita.leita.controller.dto.auth.ProblemMapper
import com.leita.leita.controller.dto.auth.response.*
import com.leita.leita.domain.problem.Problem
import com.leita.leita.repository.ProblemRepository
import org.springframework.stereotype.Service

@Service
class ProblemService(
    private val problemRepository: ProblemRepository,
) {

    fun getProblems(): List<ProblemDetailResponse> {
        val problems: List<Problem> = problemRepository.findAll()
        return problems.map{ProblemMapper.toProblemDetailResponse(it)}
    }

    fun getProblem(id: Long): ProblemDetailResponse {
        val problem: Problem = problemRepository.findById(id).get()
        return ProblemMapper.toProblemDetailResponse(problem)
    }
}