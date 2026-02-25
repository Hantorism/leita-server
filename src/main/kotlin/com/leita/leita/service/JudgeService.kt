package com.leita.leita.service

import com.leita.leita.common.exception.CustomException
import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.controller.dto.auth.JudgeMapper
import com.leita.leita.controller.dto.judge.request.SubmitRequest
import com.leita.leita.controller.dto.judge.response.SubmitResponse
import com.leita.leita.controller.dto.judge.request.RunRequest
import com.leita.leita.controller.dto.judge.response.RunResponse
import com.leita.leita.domain.judge.Judge
import com.leita.leita.domain.judge.JudgeType
import com.leita.leita.domain.judge.Result
import com.leita.leita.util.judge.JudgeUtil
import com.leita.leita.util.judge.dto.response.JudgeWCResponse
import com.leita.leita.util.judge.dto.response.RunWCResponse
import com.leita.leita.repository.JudgeRepository
import com.leita.leita.repository.ProblemRepository
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class JudgeService(
    private val judgeUtil: JudgeUtil,
    private val judgeRepository: JudgeRepository,
    private val jwtUtils: JwtUtils,
    private val problemService: ProblemService,
    private val problemRepository: ProblemRepository
) {
    @Transactional
    fun submit(problemId: Long, request: SubmitRequest): SubmitResponse {
        val user = jwtUtils.extractUser()
        val problem = problemRepository.findProblemByProblemId(problemId)
            ?: throw CustomException("Problem with id: $problemId not found", HttpStatus.NOT_FOUND)

        val submit = Judge.create(problem.id, user, request.language, JudgeType.SUBMIT)
        val submitId = judgeRepository.save(submit).id

        val response: JudgeWCResponse = judgeUtil.submit(problemId, submitId, request)
        submit.updateSizeOfCode(request.code)
        submit.updateSubmitInfo(response)

        problemService.updateSolved(problemId, response.result === Result.CORRECT)

        return JudgeMapper.toSubmitResponse(response)
    }

    @Transactional
    fun run(problemId: Long, request: RunRequest): List<RunResponse> {
        val user = jwtUtils.extractUser()
        val problem = problemRepository.findProblemByProblemId(problemId)
            ?: throw CustomException("Problem with id: $problemId not found", HttpStatus.NOT_FOUND)

        val run = Judge.create(problem.id, user, request.language, JudgeType.RUN)
        val submitId = judgeRepository.save(run).id

        val response: List<RunWCResponse> = judgeUtil.run(problemId, submitId, request)
        return JudgeMapper.toRunResponse(response)
    }

    fun getJudges(problemId: Long?): List<Judge> {
        if(problemId != null) {
            val problem = problemRepository.findProblemByProblemId(problemId)
                ?: throw CustomException("Problem with id: $problemId not found", HttpStatus.NOT_FOUND)
            return judgeRepository.findAllByProblemIdAndType(problem.id, JudgeType.SUBMIT)
        } else {
            val user = jwtUtils.extractUser()
            return judgeRepository.findAllByUserIdAndType(user.id, JudgeType.SUBMIT)
        }
    }
}