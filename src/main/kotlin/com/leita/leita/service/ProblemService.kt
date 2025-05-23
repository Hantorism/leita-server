package com.leita.leita.service

import com.leita.leita.common.exception.CustomException
import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.controller.dto.problem.ProblemMapper
import com.leita.leita.controller.dto.problem.request.CreateProblemRequest
import com.leita.leita.controller.dto.problem.request.Filter
import com.leita.leita.controller.dto.problem.response.*
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
            .orElseThrow { CustomException("Problem with id: $problemId not found", HttpStatus.NOT_FOUND) }
        if (problem.author.id != user.id) {
            throw CustomException("Permission denied", HttpStatus.FORBIDDEN)
        }
        problem.update(
            request.title, request.description, request.limit,
            request.testCases, request.source, request.category
        )

        problemRepository.save(problem)
        return CreateProblemResponse(problemId)
    }

    fun deleteProblem(problemId: Long): DeleteProblemResponse {
        val email = jwtUtils.extractEmail()
        val user = userRepository.findByEmail(email)
            ?: throw CustomException("User not found with email: $email", HttpStatus.UNAUTHORIZED)

        val problem = problemRepository.findById(problemId)
            ?: throw CustomException("Problem with id: $problemId not found", HttpStatus.NOT_FOUND)

        if(problem.get().author.id != user.id) {
            throw CustomException("Permission denied", HttpStatus.FORBIDDEN)
        }
        problemRepository.deleteById(problemId)
        return DeleteProblemResponse(true)
    }

    fun getProblems(page: Int, size: Int, search: String?, filter: Filter?): ProblemsResponse {
        val pageable: Pageable = PageRequest.of(page, size)
        var userId: Long? = null

        if(filter != null) {
            val email = jwtUtils.extractEmail()
            val user = userRepository.findByEmail(email)
                ?: throw CustomException("User not found with email: $email", HttpStatus.UNAUTHORIZED)
            userId = user.id
        }

        val problems = problemRepository.findProblemsByFilter(
            userId = userId,
            search = search,
            filter = filter?.name,
            pageable = pageable
        )
        return ProblemMapper.toProblemsResponse(problems)
    }

    fun getProblem(id: Long): ProblemDetailResponse {
        val problem = problemRepository.findById(id)
            .orElseThrow { CustomException("Problem with id: $id not found", HttpStatus.NOT_FOUND) }

        return ProblemMapper.toProblemDetailResponse(problem)
    }

    fun updateSolved(problemId: Long, isSolved: Boolean) {
        val problem = problemRepository.findById(problemId)
            .orElseThrow { CustomException("Problem with id: $problemId not found", HttpStatus.NOT_FOUND) }
        problem.solved.updateSolved(isSolved)
        problemRepository.save(problem)
    }
}