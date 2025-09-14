package com.leita.leita.port.github

import com.fasterxml.jackson.databind.JsonNode
import com.leita.leita.port.github.dto.request.GithubCommitRequest
import com.leita.leita.port.github.dto.response.GithubFileResponse
import com.leita.leita.port.github.dto.response.InstallationAccessTokenResponse
import com.leita.leita.port.github.dto.response.InstallationRepositoriesResponse
import feign.Headers
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*

@FeignClient(name = "githubClient", url = "\${external-server.github}")
interface GithubClient {
    @PostMapping("/app/installations/{installation_id}/access_tokens")
    fun createInstallationAccessToken(
        @RequestHeader("Authorization") jwt: String,
        @PathVariable("installation_id") installationId: Long
    ): InstallationAccessTokenResponse

    @GetMapping("/installation/repositories")
    @Headers("Accept: application/vnd.github+json")
    fun getInstallationRepositories(@RequestHeader("Authorization") token: String): InstallationRepositoriesResponse

    @PutMapping("/repos/{owner}/{repo}/contents/{path}")
    @Headers("Accept: application/vnd.github+json")
    fun commitFileToRepository(
        @RequestHeader("Authorization") token: String,
        @PathVariable("owner") owner: String,
        @PathVariable("repo") repo: String,
        @PathVariable("path") path: String,
        @RequestBody request: GithubCommitRequest
    ): JsonNode
}