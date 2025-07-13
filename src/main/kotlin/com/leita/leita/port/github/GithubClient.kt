package com.leita.leita.port.github

import com.fasterxml.jackson.databind.JsonNode
import com.leita.leita.port.github.dto.GithubCommitRequest
import com.leita.leita.port.github.dto.GithubFileResponse
import com.leita.leita.port.github.dto.GithubRepositoryResponse
import com.leita.leita.port.github.dto.GithubUserResponse
import com.leita.leita.port.github.dto.response.InstallationAccessTokenResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*

@FeignClient(name = "githubClient", url = "\${external-server.github}")
interface GithubClient {
    @GetMapping("/user")
    fun getUser(@RequestHeader("Authorization") token: String): GithubUserResponse

    @GetMapping("/user/repos")
    fun getRepositories(
        @RequestHeader("Authorization") token: String,
        @RequestParam("per_page") perPage: Int = 100
    ): List<GithubRepositoryResponse>

    @GetMapping("/repos/{owner}/{repo}/contents/{path}")
    fun getFileContent(
        @RequestHeader("Authorization") token: String,
        @PathVariable("owner") owner: String,
        @PathVariable("repo") repo: String,
        @PathVariable("path") path: String
    ): GithubFileResponse?

    @PutMapping("/repos/{owner}/{repo}/contents/{path}")
    fun commitFile(
        @RequestHeader("Authorization") token: String,
        @PathVariable("owner") owner: String,
        @PathVariable("repo") repo: String,
        @PathVariable("path") path: String,
        @RequestBody request: GithubCommitRequest
    )

    @PostMapping("/app/installations/{installation_id}/access_tokens")
    fun createInstallationAccessToken(
        @RequestHeader("Authorization") jwt: String,
        @PathVariable("installation_id") installationId: Long
    ): InstallationAccessTokenResponse

    @GetMapping("/installation/repositories")
    fun getInstallationRepositories(@RequestHeader("Authorization") token: String): JsonNode

    @PutMapping("/repos/{owner}/{repo}/contents/{path}")
    fun commitFileToRepository(
        @RequestHeader("Authorization") token: String,
        @PathVariable("owner") owner: String,
        @PathVariable("repo") repo: String,
        @PathVariable("path") path: String,
        @RequestBody request: GithubCommitRequest
    ): JsonNode
}