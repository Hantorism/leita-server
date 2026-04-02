package com.leita.leita.judge.controller

import com.leita.leita.common.dto.BaseResponse
import com.leita.leita.common.dto.judge.request.ReviewRequest
import com.leita.leita.common.dto.judge.request.RunRequest
import com.leita.leita.common.dto.judge.request.SubmitRequest
import com.leita.leita.common.dto.judge.response.RunResponse
import com.leita.leita.common.dto.judge.response.SubmitResponse
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
        @PathVariable problemId: Long, @RequestBody request: SubmitRequest
    ): ResponseEntity<BaseResponse<SubmitResponse>> {
        val response = judgeService.submit(problemId, request)
        val wrappedResponse: BaseResponse<SubmitResponse> = BaseResponse("제출 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @PostMapping("/run/{problemId}")
    fun run(
        @PathVariable problemId: Long, @RequestBody request: RunRequest
    ): ResponseEntity<BaseResponse<List<RunResponse>>> {
        val response = judgeService.run(problemId, request)
        val wrappedResponse: BaseResponse<List<RunResponse>> = BaseResponse("제출 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @GetMapping
    fun getJudges(
        @RequestParam(required = false) problemId: Long?
    ): ResponseEntity<BaseResponse<List<Judge>>> {
        val response = judgeService.getJudges(problemId)
        val wrappedResponse: BaseResponse<List<Judge>> = BaseResponse("", response)
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