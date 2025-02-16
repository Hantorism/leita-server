package com.leita.leita.service

import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.controller.dto.auth.ProblemMapper
import com.leita.leita.controller.dto.problem.request.CreateProblemRequest
import com.leita.leita.controller.dto.problem.request.SubmitRequest
import com.leita.leita.controller.dto.problem.response.*
import com.leita.leita.domain.problem.Problem
import com.leita.leita.domain.submit.Submit
import com.leita.leita.port.judge.JudgePort
import com.leita.leita.repository.ProblemRepository
import com.leita.leita.repository.SubmitRepository
import com.leita.leita.repository.UserRepository
import org.hibernate.query.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class ProblemService(
    private val problemRepository: ProblemRepository,
    private val jwtUtils: JwtUtils,
    private val userRepository: UserRepository
) {
    fun createProblem(request: CreateProblemRequest): CreateProblemResponse {
        val email = jwtUtils.extractEmail()
        val user = userRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("User not found with email: $email")

        val problem = ProblemMapper.fromCreateProblemRequest(user, request)
        val problemId = problemRepository.save(problem).id
        return CreateProblemResponse(problemId)
    }

    fun updateProblem(problemId: Long, request: CreateProblemRequest): CreateProblemResponse {
        val email = jwtUtils.extractEmail()
        val user = userRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("User not found with email: $email")

        val problem = problemRepository.findById(problemId)
            ?: throw Exception("Problem with id: $problemId not found")

        if(problem.get().author.id != user.id) {
            throw Exception("Permission denied: $email")
        }

        val updatedProblem = ProblemMapper.fromCreateProblemRequest(user, request)
            .update(problemId)
        problemRepository.save(updatedProblem)
        return CreateProblemResponse(problemId)
    }

    fun deleteProblem(problemId: Long): DeleteProblemResponse {
        val email = jwtUtils.extractEmail()
        val user = userRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("User not found with email: $email")

        val problem = problemRepository.findById(problemId)
            ?: throw Exception("Problem with id: $problemId not found")

        if(problem.get().author.id != user.id) {
            throw Exception("Permission denied: $email")
        }
        problemRepository.deleteById(problemId)
        return DeleteProblemResponse(true)
    }

    fun getProblems(page: Int, size: Int): ProblemsResponse {
        val pageable: Pageable = PageRequest.of(page, size)
        val problems = problemRepository.findAll(pageable)
        return ProblemMapper.toProblemsResponse(problems)
    }

    fun getProblem(id: Long): ProblemDetailResponse {
        val problem: Problem = problemRepository.findById(id).get()
        return ProblemMapper.toProblemDetailResponse(problem)
    }
}