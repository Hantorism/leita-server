package com.leita.leita.service

import com.leita.leita.common.exception.CustomException
import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.controller.dto.auth.ProblemMapper
import com.leita.leita.controller.dto.problem.request.SubmitRequest
import com.leita.leita.controller.dto.problem.response.SubmitResponse
import com.leita.leita.domain.submit.Submit
import com.leita.leita.domain.submit.UsedInfo
import com.leita.leita.port.judge.JudgePort
import com.leita.leita.repository.SubmitRepository
import com.leita.leita.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class SubmitService(
    private val judgePort: JudgePort,
    private val submitRepository: SubmitRepository,
    private val jwtUtils: JwtUtils,
    private val userRepository: UserRepository
) {
    fun submit(problemId: Long, request: SubmitRequest): SubmitResponse {
        val email = jwtUtils.extractEmail()
        val user = userRepository.findByEmail(email)
            ?: throw CustomException("User not found with email: $email", HttpStatus.UNAUTHORIZED)
        submitRepository.findById(problemId)
            ?: throw CustomException("Problem with id: $problemId not found", HttpStatus.NOT_FOUND)

        val submit = Submit( problemId, user,
            used = UsedInfo(
                memory = null,
                time = null,
                language = request.language,
            )
        )
        val submitId = submitRepository.save(submit).id

        val isSubmit: Boolean = judgePort.submit(problemId, submitId, request)
        return ProblemMapper.toSubmitResponse(isSubmit)
    }
}