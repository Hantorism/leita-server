package com.leita.leita.judge.service

import com.leita.leita.common.exception.CustomException
import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.judge.controller.JudgeMapper
import com.leita.leita.judge.dto.SubmitRequest
import com.leita.leita.judge.dto.SubmitResponse
import com.leita.leita.judge.dto.RunRequest
import com.leita.leita.judge.dto.RunResponse
import com.leita.leita.judge.domain.Judge
import com.leita.leita.judge.domain.JudgeType
import com.leita.leita.judge.domain.Result
import com.leita.leita.external.judge.JudgeUtil
import com.leita.leita.judge.dto.JudgeWCResponse
import com.leita.leita.judge.dto.RunWCResponse
import com.leita.leita.judge.repository.JudgeRepository
import com.leita.leita.problem.repository.ProblemRepository
import com.leita.leita.problem.service.ProblemService
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
        val savedSubmit = judgeRepository.save(submit)
        
        // MQ를 통해 비동기 채점 요청 발행
        judgeUtil.submitAsync(problemId, savedSubmit.id, request)
        
        // 코드 크기 업데이트
        submit.updateSizeOfCode(request.code)
        judgeRepository.save(submit)

        // 초기 응답은 PENDING 상태 또는 접수 완료 메시지 (기존 Mapper 호환을 위해 빈 response 반환 시도)
        return SubmitResponse(
            result = null,
            error = null
        )
    }

    @Transactional
    fun processJudgeResult(response: JudgeWCResponse) {
        val judge = judgeRepository.findById(response.submitId)
            .orElseThrow { CustomException("Judge with id: ${response.submitId} not found", HttpStatus.NOT_FOUND) }
        
        // 중복 처리 방지 (Idempotency)
        if (judge.result != null) {
            println("이미 처리된 채점 결과입니다: judgeId=${judge.id}")
            return
        }

        judge.updateSubmitInfo(response)
        judgeRepository.save(judge)

        problemService.updateSolved(judge.problemId, response.result === Result.CORRECT)
        println("채점 결과 처리 완료: judgeId=${judge.id}, status=${judge.result}")
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
