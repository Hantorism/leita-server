package com.leita.leita.service

import com.leita.leita.common.exception.CustomException
import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.controller.problem.ProblemMapper
import com.leita.leita.controller.problem.request.CreateProblemRequest
import com.leita.leita.controller.problem.request.Filter
import com.leita.leita.controller.problem.response.CreateProblemResponse
import com.leita.leita.controller.problem.response.DeleteProblemResponse
import com.leita.leita.controller.problem.response.ProblemDetailResponse
import com.leita.leita.controller.problem.response.ProblemsResponse
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
) {
    fun createProblem(request: CreateProblemRequest): CreateProblemResponse {
        val user = jwtUtils.extractUser()
        var problemId = Problem.generateProblemId()
        if( problemRepository.existsProblemByProblemId(problemId) ) {
            problemId = Problem.generateProblemId(problemId)
        }

        val problem = Problem.create(
            title = request.title,
            author = user,
            description = request.description,
            limit = request.limit,
            testCases = request.testCases,
            source = request.source,
            category = request.category,
            problemId
        )
        problemRepository.save(problem)

        return CreateProblemResponse(problemId)
    }

    fun updateProblem(problemId: Long, request: CreateProblemRequest): CreateProblemResponse {
        val user = jwtUtils.extractUser()
        val problem = problemRepository.findProblemByProblemId(problemId)
            ?: throw CustomException("Problem with id: $problemId not found", HttpStatus.NOT_FOUND)
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
        val user = jwtUtils.extractUser()
        val problem = problemRepository.findProblemByProblemId(problemId)
            ?: throw CustomException("Problem with id: $problemId not found", HttpStatus.NOT_FOUND)

        if(problem.author.id != user.id) {
            throw CustomException("Permission denied", HttpStatus.FORBIDDEN)
        }
        problemRepository.deleteById(problemId)
        return DeleteProblemResponse(true)
    }

    fun getProblems(page: Int, size: Int, search: String?, filter: Filter?): ProblemsResponse {
        val pageable: Pageable = PageRequest.of(page, size)
        var userId: Long? = null

        if(filter != null) {
            val user = jwtUtils.extractUser()
            userId = user.id
        }

        val problems = problemRepository.findProblemsByFilter(
            userId = userId,
            search = search,
            filter = filter?.name,
            pageable = pageable
        )
        problems.forEach { it.filterVisibleTestCases() }

        return ProblemMapper.toProblemsResponse(problems)
    }

    fun getProblem(problemId: Long): ProblemDetailResponse {
        val problem = problemRepository.findProblemByProblemId(problemId)
            ?: throw CustomException("Problem with id: $problemId not found", HttpStatus.NOT_FOUND)

        return ProblemMapper.toProblemDetailResponse(problem.filterVisibleTestCases())
    }

    fun updateSolved(problemId: Long, isSolved: Boolean) {
        val problem = problemRepository.findProblemByProblemId(problemId)
            ?: throw CustomException("Problem with id: $problemId not found", HttpStatus.NOT_FOUND)
        problem.solved.updateSolved(isSolved)
        problemRepository.save(problem.filterVisibleTestCases())
    }
}