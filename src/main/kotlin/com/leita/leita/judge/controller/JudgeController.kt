package com.leita.leita.judge.controller

import com.leita.leita.common.dto.BaseResponse
import com.leita.leita.judge.dto.ReviewRequest
import com.leita.leita.judge.dto.RunRequest
import com.leita.leita.judge.dto.SubmitRequest
import com.leita.leita.judge.dto.RunResponse
import com.leita.leita.judge.dto.SubmitResponse
import com.leita.leita.judge.domain.Judge
import com.leita.leita.git.service.GitService
import com.leita.leita.judge.service.JudgeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/judge")
class JudgeController(
    private val judgeService: JudgeService,
    private val gitService: GitService
) {
    @PostMapping("/submit/{problemId}")
    fun submit(
        @PathVariable problemId: String, @RequestBody request: SubmitRequest
    ): ResponseEntity<BaseResponse<SubmitResponse>> {
        val response = judgeService.submit(problemId, request)
        val wrappedResponse: BaseResponse<SubmitResponse> = BaseResponse("채점 요청 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @PostMapping("/run/{problemId}")
    fun run(
        @PathVariable problemId: String, @RequestBody request: RunRequest
    ): ResponseEntity<BaseResponse<List<RunResponse>>> {
        val response = judgeService.run(problemId, request)
        val wrappedResponse: BaseResponse<List<RunResponse>> = BaseResponse("코드 실행 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @GetMapping
    fun getJudges(
        @RequestParam(required = false) problemId: String?,
        @RequestParam(required = false, defaultValue = "false") userOnly: Boolean
    ): ResponseEntity<BaseResponse<List<com.leita.leita.judge.dto.JudgeDetailResponse>>> {
        val response = judgeService.getJudges(problemId, userOnly)
        val wrappedResponse: BaseResponse<List<com.leita.leita.judge.dto.JudgeDetailResponse>> = 
            BaseResponse("", JudgeMapper.toJudgeDetailResponses(response))
        return ResponseEntity.ok(wrappedResponse)
    }


    @GetMapping("/{judgeId}")
    fun getJudgeDetail(
        @PathVariable judgeId: Long
    ): ResponseEntity<BaseResponse<com.leita.leita.judge.dto.JudgeDetailResponse>> {
        val response = judgeService.getJudgeDetail(judgeId)
        val wrappedResponse: BaseResponse<com.leita.leita.judge.dto.JudgeDetailResponse> = BaseResponse("", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @PostMapping("/auto-commit")
    fun addReview(
        @RequestBody request: ReviewRequest
    ): ResponseEntity<BaseResponse<Void>> {
        gitService.autoCommit(request)
        val wrappedResponse: BaseResponse<Void> = BaseResponse("", null)
        return ResponseEntity.ok(wrappedResponse)
    }
}
