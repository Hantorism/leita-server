package com.leita.leita.controller

import com.leita.leita.controller.dto.BaseResponse
import com.leita.leita.controller.dto.problem.request.CreateProblemRequest
import com.leita.leita.controller.dto.problem.request.Filter
import com.leita.leita.controller.dto.problem.response.*
import com.leita.leita.service.ProblemService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/problem")
class ProblemController(private val problemService: ProblemService) {
    @PostMapping
    fun createProblem(@RequestBody request: CreateProblemRequest): ResponseEntity<BaseResponse<CreateProblemResponse>> {
        val response = problemService.createProblem(request)
        val wrappedResponse: BaseResponse<CreateProblemResponse> = BaseResponse("문제 추가 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @PatchMapping("/{id}")
    fun updateProblem(@PathVariable id: Long, @RequestBody request: CreateProblemRequest): ResponseEntity<BaseResponse<CreateProblemResponse>> {
        val response = problemService.updateProblem(id, request)
        val wrappedResponse: BaseResponse<CreateProblemResponse> = BaseResponse("문제 추가 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @DeleteMapping("/{id}")
    fun deleteProblem(@PathVariable id: Long): ResponseEntity<BaseResponse<DeleteProblemResponse>> {
        val response = problemService.deleteProblem(id)
        val wrappedResponse: BaseResponse<DeleteProblemResponse> = BaseResponse("문제 삭제 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @GetMapping
    fun getProblems(
        @RequestParam page: Int = 0, @RequestParam size: Int = 10,
        @RequestParam(required = false) search: String? = null,
        @RequestParam(required = false) filter: Filter? = null
    ): ResponseEntity<BaseResponse<ProblemsResponse>> {
        val response = problemService.getProblems(page, size, search, filter)
        val wrappedResponse: BaseResponse<ProblemsResponse> = BaseResponse("문제 조회 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @GetMapping("/{id}")
    fun getProblem(@PathVariable id: Long): ResponseEntity<BaseResponse<ProblemDetailResponse>> {
        val response = problemService.getProblem(id)
        val wrappedResponse: BaseResponse<ProblemDetailResponse> = BaseResponse("문제 조회 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }
}