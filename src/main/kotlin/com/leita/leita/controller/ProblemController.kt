package com.leita.leita.controller

import com.leita.leita.controller.dto.problem.request.SubmitRequest
import com.leita.leita.controller.dto.problem.response.SubmitResponse
import com.leita.leita.controller.dto.problem.response.ProblemDetailResponse
import com.leita.leita.service.ProblemService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/problem")
class ProblemController(private val problemService: ProblemService) {

    @GetMapping
    fun getProblems(): ResponseEntity<List<ProblemDetailResponse>> {
        val response = problemService.getProblems()
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getProblem(@PathVariable id: Long): ResponseEntity<ProblemDetailResponse> {
        val response = problemService.getProblem(id)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{id}/submit")
    fun submit(@PathVariable id: Long, @RequestBody request: SubmitRequest): ResponseEntity<SubmitResponse> {
        val response = problemService.submit(id, request)
        return ResponseEntity.ok(response)
    }
}