package com.leita.leita.controller.autoCommit

import com.fasterxml.jackson.databind.JsonNode
import com.leita.leita.common.exception.CustomException
import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.controller.dto.BaseResponse
import com.leita.leita.port.github.GithubPort
import com.leita.leita.port.github.dto.GithubCommitRequest
import com.leita.leita.repository.UserRepository
import com.leita.leita.service.AutoCommitService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView


@RestController
@RequestMapping("/auto-commit")
class AutoCommitController(
    private val autoCommitService: AutoCommitService,
    private val githubAppPort: GithubPort,
    private val jwtUtils: JwtUtils,
    private val userRepository: UserRepository
) {
    @PostMapping("/{problemId}/commit")
    fun commit(@PathVariable problemId: Long, @RequestParam repositoryName: String): ResponseEntity<BaseResponse<Void>> {
        autoCommitService.commit(problemId, repositoryName)
        val wrappedResponse: BaseResponse<Void> = BaseResponse("Commit 완료", null)
        return ResponseEntity.ok(wrappedResponse)
    }

    @GetMapping("/install/callback")
    fun handleGitHubAppInstallationCallback(
        @RequestParam("installation_id") installationId: Long?,
        @RequestParam(value = "setup_action", required = false) setupAction: String?,
        @RequestParam(value = "state", required = false) state: String?
    ): RedirectView {
        val user = jwtUtils.extractUser()
        user.installationId = installationId
        userRepository.save(user)

        return RedirectView("/dashboard?githubAppInstalled=true")
    }

    @GetMapping("/repositories")
    @ResponseBody
    fun getInstalledRepositories(): ResponseEntity<JsonNode> {
        val user = jwtUtils.extractUser()
        val installationId = user.installationId ?: throw CustomException("GitHub App not installed", HttpStatus.BAD_REQUEST)

        try {
            val installationAccessToken = githubAppPort.getInstallationAccessToken(installationId)
            val repositories = githubAppPort.getInstallationRepositories(installationAccessToken)
            return ResponseEntity(repositories, HttpStatus.OK)
        } catch (e: Exception) {
            System.err.println("설치된 레포지토리 목록 가져오기 실패: " + e.message)
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PostMapping("/commit")
    @ResponseBody
    fun commitFile(@RequestBody commitRequest: GithubCommitRequest): ResponseEntity<JsonNode> {
        val user = jwtUtils.extractUser()
        val installationId = user.installationId ?: throw CustomException("GitHub App not installed", HttpStatus.BAD_REQUEST)

        try {
            val installationAccessToken = githubAppPort.getInstallationAccessToken(installationId)

            val commitResult = githubAppPort.commitFileToRepository(
                token = installationAccessToken,
                owner = user.githubInfo?.userName ?: throw CustomException("Github user not found", HttpStatus.BAD_REQUEST),
                repo = commitRequest.repo,
                path = "README.md",
                message = commitRequest.message,
                content = commitRequest.content,
                sha = commitRequest.sha,
                branch = commitRequest.branch,
                authorName = user.name,
                authorEmail = user.email
            )
            return ResponseEntity(commitResult, HttpStatus.OK)
        } catch (e: Exception) {
            System.err.println("파일 커밋 실패: " + e.message)
            e.printStackTrace()
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}