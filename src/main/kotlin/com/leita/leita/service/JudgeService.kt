package com.leita.leita.service

import com.leita.leita.common.exception.CustomException
import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.controller.dto.auth.ProblemMapper
import com.leita.leita.controller.dto.judge.request.SubmitRequest
import com.leita.leita.controller.dto.problem.response.SubmitResponse
import com.leita.leita.controller.dto.judge.request.RunRequest
import com.leita.leita.domain.judge.Judge
import com.leita.leita.domain.judge.JudgeType
import com.leita.leita.domain.judge.UsedInfo
import com.leita.leita.port.judge.JudgePort
import com.leita.leita.repository.JudgeRepository
import com.leita.leita.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class JudgeService(
    private val judgePort: JudgePort,
    private val judgeRepository: JudgeRepository,
    private val jwtUtils: JwtUtils,
    private val userRepository: UserRepository
) {
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

        val isSubmit: Boolean = judgePort.submit(problemId, submitId, request)
        return ProblemMapper.toSubmitResponse(isSubmit)
    }

    fun run(problemId: Long, request: RunRequest): SubmitResponse {
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

        val isSubmit: Boolean = judgePort.run(problemId, submitId, request)
        return ProblemMapper.toSubmitResponse(isSubmit)
    }
}