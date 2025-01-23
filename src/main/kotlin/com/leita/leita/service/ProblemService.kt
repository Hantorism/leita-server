package com.leita.leita.service

import com.leita.leita.controller.dto.auth.ProblemMapper
import com.leita.leita.controller.dto.auth.request.SubmitRequest
import com.leita.leita.controller.dto.auth.request.SubmitResponse
import com.leita.leita.controller.dto.auth.response.*
import com.leita.leita.domain.problem.Problem
import com.leita.leita.repository.ProblemRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*

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

    fun submit(request: SubmitRequest): SubmitResponse {
        // judgePort 추가
        val isSubmit: Boolean = true;
        return ProblemMapper.toSubmitResponse(isSubmit)
    }
}