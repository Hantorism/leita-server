package com.leita.leita.controller

import com.leita.leita.controller.dto.auth.response.*
import com.leita.leita.service.ProblemService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}