package com.leita.leita.controller

import com.leita.leita.controller.dto.auth.request.SubmitRequest
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

    @PostMapping("/submit")
    fun submit(@RequestBody request: SubmitRequest): ResponseEntity<SubmitResponse> {
        val response = problemService.submit(request)
        return ResponseEntity.ok(response)
    }
}