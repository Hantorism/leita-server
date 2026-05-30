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
import com.leita.leita.judge.util.JudgeUtil
import com.leita.leita.judge.dto.JudgeWCResponse
import com.leita.leita.judge.dto.RunWCResponse
import com.leita.leita.judge.event.ProblemJudgedEvent
import com.leita.leita.judge.repository.JudgeRepository
import com.leita.leita.problem.repository.ProblemRepository
import com.leita.leita.problem.service.ProblemService
import jakarta.transaction.Transactional
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class JudgeService(
    private val judgeUtil: JudgeUtil,
    private val judgeRepository: JudgeRepository,
    private val jwtUtils: JwtUtils,
    private val problemService: ProblemService,
    private val problemRepository: ProblemRepository,
    private val eventPublisher: ApplicationEventPublisher,
    private val oracleStorageUtil: com.leita.leita.file.util.OracleStorageUtil
) {
    @Transactional
    fun submit(problemId: String, request: SubmitRequest): SubmitResponse {
        val user = jwtUtils.extractUser()
        val problem = problemRepository.findProblemByProblemId(problemId)
            ?: throw CustomException("Problem with id: $problemId not found", HttpStatus.NOT_FOUND)

        var submit = Judge.create(problem.problemId, user, request.language, JudgeType.SUBMIT)
        submit.result = Result.PENDING
        submit = judgeRepository.saveAndFlush(submit)
        val submitId = submit.id

        // ✅ 채점 서버가 업로드할 경로 규칙에 맞게 URL 조립
        val extension = request.language.toExtension()
        val codePath = "submits/$submitId/Main.$extension"
        val codeUrl = "https://objectstorage.ap-chuncheon-1.oraclecloud.com/n/${oracleStorageUtil.getNamespace()}/b/${oracleStorageUtil.getBucketName()}/o/$codePath"
        
        submit.updateCodeUrl(codeUrl)
        submit.updateSizeOfCode(request.code)
        judgeRepository.save(submit)

        // 외부 채점 서버에 요청 (ack만 수신)
        judgeUtil.submit(problemId, submitId, request, problem.limit)

        return SubmitResponse(submitId = submitId, result = Result.PENDING, error = "")
    }

    @Transactional
    fun completeJudge(submitId: Long, response: JudgeWCResponse) {
        val submit = judgeRepository.findById(submitId).orElseThrow {
            CustomException("Judge with id: $submitId not found", HttpStatus.NOT_FOUND)
        }

        if (submit.result != null && submit.result != Result.PENDING) return

        submit.updateSubmitInfo(response)
        judgeRepository.save(submit)

        problemService.updateSolved(submit.problemId, response.result === Result.CORRECT)
        
        eventPublisher.publishEvent(ProblemJudgedEvent(submit.user.id, submit.problemId))
    }

    @Transactional
    fun run(problemId: String, request: RunRequest): List<RunResponse> {
        val user = jwtUtils.extractUser()
        val problem = problemRepository.findProblemByProblemId(problemId)
            ?: throw CustomException("Problem with id: $problemId not found", HttpStatus.NOT_FOUND)

        val run = Judge.create(problem.problemId, user, request.language, JudgeType.RUN)
        val submitId = judgeRepository.save(run).id

        val response: List<RunWCResponse> = judgeUtil.run(problemId, submitId, request, problem.limit)
        return JudgeMapper.toRunResponse(response)
    }

    fun getJudges(problemId: String?, userOnly: Boolean): List<Judge> {
        if(problemId != null) {
            val problem = problemRepository.findProblemByProblemId(problemId)
                ?: throw CustomException("Problem with id: $problemId not found", HttpStatus.NOT_FOUND)
            return judgeRepository.findAllByProblemIdAndTypeOrderByCreatedAtDesc(problem.problemId, JudgeType.SUBMIT)
        }
        
        if (userOnly) {
            val user = jwtUtils.extractUser()
            return judgeRepository.findAllByUserIdAndTypeOrderByCreatedAtDesc(user.id, JudgeType.SUBMIT)
        }
        
        return judgeRepository.findAllByTypeOrderByCreatedAtDesc(JudgeType.SUBMIT)
    }

    fun getJudgeDetail(judgeId: Long): com.leita.leita.judge.dto.JudgeDetailResponse {
        val judge = judgeRepository.findById(judgeId).orElseThrow {
            CustomException("Judge with id: $judgeId not found", HttpStatus.NOT_FOUND)
        }
        val user = jwtUtils.extractUser()
        if (judge.user.id != user.id) {
            throw CustomException("Permission denied", HttpStatus.FORBIDDEN)
        }
        return JudgeMapper.toJudgeDetailResponse(judge)
    }
}
