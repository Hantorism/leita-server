package com.leita.leita.controller

import com.leita.leita.controller.dto.study.request.StudyCreateRequest
import com.leita.leita.controller.dto.study.response.*
import com.leita.leita.service.StudyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/study")
class StudyController(private val studyService: StudyService) {

    @GetMapping
    fun getStudies(): ResponseEntity<List<StudyDetailResponse>> {
        val response = studyService.getStudies()
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getStudy(@PathVariable id: Long): ResponseEntity<StudyDetailResponse> {
        val response = studyService.getStudy(id)
        return ResponseEntity.ok(response)
    }

    @PostMapping
    fun createStudy(@RequestBody request: StudyCreateRequest): ResponseEntity<StudyCreateResponse> {
        val response = studyService.create(request)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}/join")
    fun join(@PathVariable id: Long): ResponseEntity<StudyJoinResponse> {
        val response = studyService.join(id)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}/approve")
    fun approve(@PathVariable id: Long, @RequestParam email: String): ResponseEntity<StudyApproveResponse> {
        val response = studyService.approve(id, email)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}/deny")
    fun deny(@PathVariable id: Long, @RequestParam email: String): ResponseEntity<StudyDenyResponse> {
        val response = studyService.deny(id, email)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}/leave")
    fun approve(@PathVariable id: Long): ResponseEntity<StudyLeaveResponse> {
        val response = studyService.leave(id)
        return ResponseEntity.ok(response)
    }
}