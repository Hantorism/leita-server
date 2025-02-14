package com.leita.leita.controller

import com.leita.leita.controller.dto.problem.request.SubmitRequest
import com.leita.leita.controller.dto.problem.response.SubmitResponse
import com.leita.leita.service.SubmitService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/submit")
class SubmitController(private val submitService: SubmitService) {
    @PostMapping("/{problemId}")
    fun submit(@PathVariable problemId: Long, @RequestBody request: SubmitRequest): ResponseEntity<SubmitResponse> {
        val response = submitService.submit(problemId, request)
        return ResponseEntity.ok(response)
    }
}