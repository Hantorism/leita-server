package com.leita.leita.controller

import com.leita.leita.controller.dto.BaseResponse
import com.leita.leita.controller.dto.problem.response.SubmitResponse
import com.leita.leita.controller.dto.judge.request.SubmitRequest
import com.leita.leita.controller.dto.judge.request.RunRequest
import com.leita.leita.service.JudgeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/judge")
class JudgeController(private val judgeService: JudgeService) {
    @PostMapping("/submit/{problemId}")
    fun submit(@PathVariable problemId: Long, @RequestBody request: SubmitRequest): ResponseEntity<BaseResponse<SubmitResponse>> {
        val response = judgeService.submit(problemId, request)
        val wrappedResponse: BaseResponse<SubmitResponse> = BaseResponse("제출 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @PostMapping("/run/{problemId}")
    fun run(@PathVariable problemId: Long, @RequestBody request: RunRequest): ResponseEntity<BaseResponse<SubmitResponse>> {
        val response = judgeService.run(problemId, request)
        val wrappedResponse: BaseResponse<SubmitResponse> = BaseResponse("제출 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }
}