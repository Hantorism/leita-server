package com.leita.leita.file.controller

import com.leita.leita.file.dto.GeneratePARRequest
import com.leita.leita.file.dto.GeneratePARResponse
import com.leita.leita.file.service.FileService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/files")
class FileController(
    private val fileService: FileService
) {
    @PostMapping("/par")
    fun generatePAR(@RequestBody request: GeneratePARRequest): GeneratePARResponse {
        val url = fileService.createPreAuthenticatedRequest(request.objectName)
        return GeneratePARResponse(url)
    }
}
