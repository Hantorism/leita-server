package com.leita.leita.controller

import com.leita.leita.controller.dto.submit.request.JudgeSubmitRequest
import com.leita.leita.controller.dto.problem.response.SubmitResponse
import com.leita.leita.controller.dto.submit.request.RunSubmitRequest
import com.leita.leita.service.SubmitService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/submit")
class SubmitController(private val submitService: SubmitService) {
    @PostMapping("/judge/{problemId}")
    fun judgeSubmit(@PathVariable problemId: Long, @RequestBody request: JudgeSubmitRequest): ResponseEntity<SubmitResponse> {
        val response = submitService.judgeSubmit(problemId, request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/run/{problemId}")
    fun runSubmit(@PathVariable problemId: Long, @RequestBody request: RunSubmitRequest): ResponseEntity<SubmitResponse> {
        val response = submitService.runSubmit(problemId, request)
        return ResponseEntity.ok(response)
    }
}