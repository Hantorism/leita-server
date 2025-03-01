package com.leita.leita.service

import com.leita.leita.common.exception.CustomException
import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.controller.dto.auth.ProblemMapper
import com.leita.leita.controller.dto.submit.request.JudgeSubmitRequest
import com.leita.leita.controller.dto.problem.response.SubmitResponse
import com.leita.leita.controller.dto.submit.request.RunSubmitRequest
import com.leita.leita.domain.submit.Submit
import com.leita.leita.domain.submit.SubmitType
import com.leita.leita.domain.submit.UsedInfo
import com.leita.leita.port.submit.SubmitPort
import com.leita.leita.repository.SubmitRepository
import com.leita.leita.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class SubmitService(
    private val submitPort: SubmitPort,
    private val submitRepository: SubmitRepository,
    private val jwtUtils: JwtUtils,
    private val userRepository: UserRepository
) {
    fun judgeSubmit(problemId: Long, request: JudgeSubmitRequest): SubmitResponse {
        val email = jwtUtils.extractEmail()
        val user = userRepository.findByEmail(email)
            ?: throw CustomException("User not found with email: $email", HttpStatus.UNAUTHORIZED)
        submitRepository.findById(problemId)
            ?: throw CustomException("Problem with id: $problemId not found", HttpStatus.NOT_FOUND)

        val submit = Submit(
            problemId,
            user,
            used = UsedInfo(
                memory = null,
                time = null,
                language = request.language,
            ),
            type = SubmitType.JUDGE
        )
        val submitId = submitRepository.save(submit).id

        val isSubmit: Boolean = submitPort.judgeSubmit(problemId, submitId, request)
        return ProblemMapper.toSubmitResponse(isSubmit)
    }

    fun runSubmit(problemId: Long, request: RunSubmitRequest): SubmitResponse {
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
            ),
            type = SubmitType.RUN
        )
        val submitId = submitRepository.save(submit).id

        val isSubmit: Boolean = submitPort.runSubmit(problemId, submitId, request)
        return ProblemMapper.toSubmitResponse(isSubmit)
    }


}