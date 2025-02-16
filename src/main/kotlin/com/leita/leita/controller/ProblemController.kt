package com.leita.leita.controller

import com.leita.leita.controller.dto.problem.request.CreateProblemRequest
import com.leita.leita.controller.dto.problem.request.SubmitRequest
import com.leita.leita.controller.dto.problem.response.*
import com.leita.leita.service.ProblemService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/problem")
class ProblemController(private val problemService: ProblemService) {
    @PostMapping
    fun createProblem(@RequestBody request: CreateProblemRequest): ResponseEntity<CreateProblemResponse> {
        val response = problemService.createProblem(request)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{id}")
    fun updateProblem(@PathVariable id: Long, @RequestBody request: CreateProblemRequest): ResponseEntity<CreateProblemResponse> {
        val response = problemService.updateProblem(id, request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun deleteProblem(@PathVariable id: Long): ResponseEntity<DeleteProblemResponse> {
        val response = problemService.deleteProblem(id)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getProblems(
        @RequestParam page: Int = 0, @RequestParam size: Int = 10
    ): ResponseEntity<ProblemsResponse> {
        val response = problemService.getProblems(page, size)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getProblem(@PathVariable id: Long): ResponseEntity<ProblemDetailResponse> {
        val response = problemService.getProblem(id)
        return ResponseEntity.ok(response)
    }
}