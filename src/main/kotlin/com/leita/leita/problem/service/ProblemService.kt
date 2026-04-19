package com.leita.leita.problem.service

import com.leita.leita.common.dto.TestCaseDto
import com.leita.leita.common.exception.CustomException
import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.problem.controller.ProblemMapper
import com.leita.leita.problem.dto.CreateProblemRequest
import com.leita.leita.problem.dto.Filter
import com.leita.leita.problem.dto.CreateProblemResponse
import com.leita.leita.problem.dto.DeleteProblemResponse
import com.leita.leita.problem.dto.ProblemDetailResponse
import com.leita.leita.problem.dto.ProblemsResponse
import com.leita.leita.problem.domain.Description
import com.leita.leita.problem.domain.Problem
import com.leita.leita.problem.domain.TestCase
import com.leita.leita.file.util.OracleStorageUtil
import com.leita.leita.problem.repository.ProblemRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ProblemService(
    private val problemRepository: ProblemRepository,
    private val jwtUtils: JwtUtils,
    private val oracleStorageUtil: OracleStorageUtil,
) {
    fun createProblem(request: CreateProblemRequest): CreateProblemResponse {
        val user = jwtUtils.extractUser()
        var problemId = Problem.generateProblemId()
        if (problemRepository.existsProblemByProblemId(problemId)) {
            problemId = Problem.generateProblemId(problemId)
        }

        val description = uploadDescription(problemId, request.description)
        val testCases = uploadTestCases(problemId, request.testCases)

        val problem = Problem.create(
            title = request.title,
            author = user,
            description = description,
            limit = request.limit,
            testCases = testCases,
            source = request.source,
            category = request.category,
            problemId = problemId
        )

        problemRepository.save(problem)

        return CreateProblemResponse(problemId)
    }

    fun updateProblem(problemId: String, request: CreateProblemRequest): CreateProblemResponse {
        val user = jwtUtils.extractUser()
        val problem = problemRepository.findProblemByProblemId(problemId)
            ?: throw CustomException("Problem with id: $problemId not found", HttpStatus.NOT_FOUND)
        if (problem.author.id != user.id) {
            throw CustomException("Permission denied", HttpStatus.FORBIDDEN)
        }

        deleteProblemFiles(problemId)
        val description = uploadDescription(problemId, request.description)
        val testCases = uploadTestCases(problemId, request.testCases)

        problem.update(
            request.title, description, request.limit,
            testCases, request.source, request.category
        )

        problemRepository.save(problem)
        return CreateProblemResponse(problemId)
    }

    fun deleteProblem(problemId: String): DeleteProblemResponse {
        val user = jwtUtils.extractUser()
        val problem = problemRepository.findProblemByProblemId(problemId)
            ?: throw CustomException("Problem with id: $problemId not found", HttpStatus.NOT_FOUND)

        if (problem.author.id != user.id) {
            throw CustomException("Permission denied", HttpStatus.FORBIDDEN)
        }

        // ✅ 오브젝트 스토리지 내 관련 파일 전부 삭제
        deleteProblemFiles(problemId)

        problemRepository.delete(problem)
        return DeleteProblemResponse(true)
    }

    fun getProblems(page: Int, size: Int, search: String?, filter: Filter?): ProblemsResponse {
        val pageable: Pageable = PageRequest.of(page, size)
        var userId: Long? = null

        if (filter != null) {
            val user = jwtUtils.extractUser()
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

fun getProblem(problemId: String): ProblemDetailResponse {
    val problem = problemRepository.findProblemByProblemId(problemId)
        ?: throw CustomException("Problem with id: $problemId not found", HttpStatus.NOT_FOUND)

    val problemContent = if (problem.description.problem.startsWith("http")) {
        try {
            oracleStorageUtil.readString(oracleStorageUtil.extractObjectName(problem.description.problem))
        } catch (e: Exception) {
            "Error loading problem description from storage: ${e.message}"
        }
    } else {
        problem.description.problem
    }

    val inputContent = if (problem.description.input.startsWith("http")) {
        try {
            oracleStorageUtil.readString(oracleStorageUtil.extractObjectName(problem.description.input))
        } catch (e: Exception) {
            "Error loading input description from storage: ${e.message}"
        }
    } else {
        problem.description.input
    }

    val outputContent = if (problem.description.output.startsWith("http")) {
        try {
            oracleStorageUtil.readString(oracleStorageUtil.extractObjectName(problem.description.output))
        } catch (e: Exception) {
            "Error loading output description from storage: ${e.message}"
        }
    } else {
        problem.description.output
    }

        val fetchedDescription = Description(
            problem = problemContent,
            input = inputContent,
            output = outputContent
        )

        val testCaseDtos = problem.testCases.filter { it.isShow }.map { testCase ->
            val tcInputContent = if (testCase.input.startsWith("http")) {
                try {
                    oracleStorageUtil.readString(oracleStorageUtil.extractObjectName(testCase.input))
                } catch (e: Exception) {
                    "Error loading input: ${e.message}"
                }
            } else {
                testCase.input
            }

            val tcOutputContent = if (testCase.output.startsWith("http")) {
                try {
                    oracleStorageUtil.readString(oracleStorageUtil.extractObjectName(testCase.output))
                } catch (e: Exception) {
                    "Error loading output: ${e.message}"
                }
            } else {
                testCase.output
            }

            TestCaseDto(
                input = tcInputContent,
                output = tcOutputContent,
                isShow = testCase.isShow
            )
        }

        return ProblemMapper.toProblemDetailResponse(problem).copy(
            description = fetchedDescription,
            testCases = testCaseDtos
        )
    }

    fun updateSolved(problemId: String, isSolved: Boolean) {
        val problem = problemRepository.findProblemByProblemId(problemId)
            ?: throw CustomException("Problem with id: $problemId not found", HttpStatus.NOT_FOUND)
        problem.solved.updateSolved(isSolved)
        problemRepository.save(problem)
    }

    private fun uploadDescription(problemId: String, description: Description): Description {
        val basePath = "problems/$problemId/descriptions"
        return Description.create(
            oracleStorageUtil.uploadString("$basePath/problem.html", description.problem),
            oracleStorageUtil.uploadString("$basePath/input.html", description.input),
            oracleStorageUtil.uploadString("$basePath/output.html", description.output)
        )
    }

    private fun uploadTestCases(problemId: String, testCases: List<TestCaseDto>): List<TestCase> {
        val basePath = "problems/$problemId/testcases"
        return testCases.mapIndexed { index, dto ->
            val inputUrl = oracleStorageUtil.uploadString("$basePath/$index.in", dto.input)
            val outputUrl = oracleStorageUtil.uploadString("$basePath/$index.out", dto.output)
            TestCase(input = inputUrl, output = outputUrl, isShow = dto.isShow)
        }
    }

    private fun deleteProblemFiles(problemId: String) {
        val basePath = "problems/$problemId/"
        oracleStorageUtil.deleteFolder(basePath)
    }
}