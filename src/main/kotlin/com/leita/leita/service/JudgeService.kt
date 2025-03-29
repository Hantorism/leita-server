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
import com.leita.leita.domain.judge.UsedInfo
import com.leita.leita.port.judge.JudgePort
import com.leita.leita.port.judge.dto.response.JudgeWCResponse
import com.leita.leita.repository.JudgeRepository
import com.leita.leita.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class JudgeService(
    private val judgePort: JudgePort,
    private val judgeRepository: JudgeRepository,
    private val jwtUtils: JwtUtils,
    private val userRepository: UserRepository,
    private val problemService: ProblemService
) {
    @Transactional
    fun submit(problemId: Long, request: SubmitRequest): SubmitResponse {
        val email = jwtUtils.extractEmail()
        val user = userRepository.findByEmail(email)
            ?: throw CustomException("User not found with email: $email", HttpStatus.UNAUTHORIZED)
        judgeRepository.findById(problemId)
            ?: throw CustomException("Problem with id: $problemId not found", HttpStatus.NOT_FOUND)

        val submit = Judge(
            problemId,
            user,
            used = UsedInfo(
                memory = null,
                time = null,
                language = request.language,
            ),
            type = JudgeType.SUBMIT
        )
        val submitId = judgeRepository.save(submit).id

        val response: JudgeWCResponse = judgePort.submit(problemId, submitId, request)
        problemService.updateSolved(problemId, response.result === Result.CORRECT)

        return JudgeMapper.toSubmitResponse(response)
    }

    @Transactional
    fun run(problemId: Long, request: RunRequest): List<RunResponse> {
        val email = jwtUtils.extractEmail()
        val user = userRepository.findByEmail(email)
            ?: throw CustomException("User not found with email: $email", HttpStatus.UNAUTHORIZED)
        judgeRepository.findById(problemId)
            ?: throw CustomException("Problem with id: $problemId not found", HttpStatus.NOT_FOUND)

        val submit = Judge(
            problemId,
            user,
            used = UsedInfo(
                memory = null,
                time = null,
                language = request.language,
            ),
            type = JudgeType.RUN
        )
        val submitId = judgeRepository.save(submit).id

        val response: List<JudgeWCResponse> = judgePort.run(problemId, submitId, request)
        return JudgeMapper.toRunResponse(response)
    }

    fun getJudges(problemId: Long?): List<Judge> {
        if(problemId != null) {
            return judgeRepository.findAllByProblemIdAndType(problemId, JudgeType.SUBMIT)
        } else {
            val email = jwtUtils.extractEmail()
            val user = userRepository.findByEmail(email)
                ?: throw CustomException("User not found with email: $email", HttpStatus.UNAUTHORIZED)
            return judgeRepository.findAllByUserIdAndType(user.id, JudgeType.SUBMIT)
        }
    }
}