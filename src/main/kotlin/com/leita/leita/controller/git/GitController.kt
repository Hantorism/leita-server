package com.leita.leita.controller.git

import com.leita.leita.common.exception.CustomException
import com.leita.leita.controller.dto.BaseResponse
import com.leita.leita.controller.git.request.CommitRequest
import com.leita.leita.controller.git.response.GitInstallResponse
import com.leita.leita.controller.git.response.RepositoryResponse
import com.leita.leita.service.GitService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/git")
class GitController(
    private val gitService: GitService,
) {

    @GetMapping("/install")
    fun getInstallationUrl(): ResponseEntity<GitInstallResponse> {
        val installationUrl = gitService.createInstallationUrl()
        return ResponseEntity.ok(GitInstallResponse(installationUrl))
    }

    @GetMapping("/install/callback")
    fun callbackInstall(
        @RequestParam("installation_id") installationId: Long?,
        @RequestParam(value = "setup_action", required = false) setupAction: String?,
        @RequestParam(value = "state", required = false) state: String?
    ): ResponseEntity<String> {
        if (installationId == null || state == null) {
            throw CustomException("Github Apps 설치 실패", HttpStatus.BAD_REQUEST)
        }
        gitService.callbackInstall(installationId, state)

        val successHtml = "<script>window.close();</script>"
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .body(successHtml)
    }

    @PostMapping("/commit")
    fun commit(@RequestBody request: CommitRequest): ResponseEntity<BaseResponse<Void>> {
        gitService.commit(request)
        return ResponseEntity.ok(null)
    }

    @GetMapping("/repositories")
    @ResponseBody
    fun getInstalledRepositories(): ResponseEntity<List<RepositoryResponse>> {
        val repositories: List<RepositoryResponse> = gitService.getRepositories()
        return ResponseEntity.ok(repositories)
    }
}