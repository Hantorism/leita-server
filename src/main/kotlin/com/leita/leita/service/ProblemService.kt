package com.leita.leita.service

import com.leita.leita.common.exception.CustomException
import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.controller.dto.auth.ProblemMapper
import com.leita.leita.controller.dto.problem.request.CreateProblemRequest
import com.leita.leita.controller.dto.problem.response.*
import com.leita.leita.domain.problem.Problem
import com.leita.leita.repository.ProblemRepository
import com.leita.leita.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
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
            ?: throw CustomException("User not found with email: $email", HttpStatus.UNAUTHORIZED)

        val problem = ProblemMapper.fromCreateProblemRequest(user, request)
        val problemId = problemRepository.save(problem).id
        return CreateProblemResponse(problemId)
    }

    fun updateProblem(problemId: Long, request: CreateProblemRequest): CreateProblemResponse {
        val email = jwtUtils.extractEmail()
        val user = userRepository.findByEmail(email)
            ?: throw CustomException("User not found with email: $email", HttpStatus.UNAUTHORIZED)

        val problem = problemRepository.findById(problemId)
            ?: throw CustomException("Problem with id: $problemId not found", HttpStatus.NOT_FOUND)

        if(problem.get().author.id != user.id) {
            throw CustomException("Permission denied: $email", HttpStatus.FORBIDDEN)
        }

        val updatedProblem = ProblemMapper.fromCreateProblemRequest(user, request)
            .update(problemId)
        problemRepository.save(updatedProblem)
        return CreateProblemResponse(problemId)
    }

    fun deleteProblem(problemId: Long): DeleteProblemResponse {
        val email = jwtUtils.extractEmail()
        val user = userRepository.findByEmail(email)
            ?: throw CustomException("User not found with email: $email", HttpStatus.UNAUTHORIZED)

        val problem = problemRepository.findById(problemId)
            ?: throw CustomException("Problem with id: $problemId not found", HttpStatus.NOT_FOUND)

        if(problem.get().author.id != user.id) {
            throw CustomException("Permission denied: $email", HttpStatus.FORBIDDEN)
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