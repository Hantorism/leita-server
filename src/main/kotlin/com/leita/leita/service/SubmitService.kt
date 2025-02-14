package com.leita.leita.service

import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.controller.dto.auth.ProblemMapper
import com.leita.leita.controller.dto.problem.request.SubmitRequest
import com.leita.leita.controller.dto.problem.response.SubmitResponse
import com.leita.leita.domain.submit.Submit
import com.leita.leita.port.judge.JudgePort
import com.leita.leita.repository.SubmitRepository
import com.leita.leita.repository.UserRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
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
            ?: throw UsernameNotFoundException("User not found with email: $email")
        submitRepository.findById(problemId)
            ?: throw Exception("Problem with id: $problemId not found")

        val submit = Submit( problemId, user )
        val submitId = submitRepository.save(submit).id

        val isSubmit: Boolean = judgePort.submit(problemId, submitId, request);
        return ProblemMapper.toSubmitResponse(isSubmit)
    }
}