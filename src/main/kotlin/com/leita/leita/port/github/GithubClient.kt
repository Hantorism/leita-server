package com.leita.leita.port.github

import com.leita.leita.port.github.dto.request.CreateCommitRequest
import com.leita.leita.port.github.dto.request.CreateRefRequest
import com.leita.leita.port.github.dto.request.GithubCommitRequest
import com.leita.leita.port.github.dto.request.GithubTreeRequest
import com.leita.leita.port.github.dto.request.UpdateRefRequest
import com.leita.leita.port.github.dto.response.*
import feign.Headers
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RequestParam

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
    ): GithubCommitResponse

    @GetMapping("/repos/{owner}/{repo}/git/refs/heads/{branch}")
    @Headers("Accept: application/vnd.github+json")
    fun getRef(
        @RequestHeader("Authorization") token: String,
        @PathVariable("owner") owner: String,
        @PathVariable("repo") repo: String,
        @PathVariable("branch") branch: String
    ): GithubRefResponse

    @PostMapping("/repos/{owner}/{repo}/git/trees")
    @Headers("Accept: application/vnd.github+json")
    fun createTree(
        @RequestHeader("Authorization") token: String,
        @PathVariable("owner") owner: String,
        @PathVariable("repo") repo: String,
        @RequestBody request: GithubTreeRequest
    ): GithubTreeResponse

    @PostMapping("/repos/{owner}/{repo}/git/commits")
    @Headers("Accept: application/vnd.github+json")
    fun createCommit(
        @RequestHeader("Authorization") token: String,
        @PathVariable("owner") owner: String,
        @PathVariable("repo") repo: String,
        @RequestBody request: CreateCommitRequest
    ): CreateCommitResponse

    @RequestMapping(
        method = [RequestMethod.PATCH],
        value = ["/repos/{owner}/{repo}/git/refs/heads/{branch}"],
        headers = ["Accept: application/vnd.github+json"]
    )
    fun updateRef(
        @RequestHeader("Authorization") token: String,
        @PathVariable("owner") owner: String,
        @PathVariable("repo") repo: String,
        @PathVariable("branch") branch: String,
        @RequestBody request: UpdateRefRequest
    ): GithubRefResponse

    @GetMapping("/app/installations/{installation_id}")
    @Headers("Accept: application/vnd.github+json")
    fun getInstallation(
        @RequestHeader("Authorization") jwt: String,
        @PathVariable("installation_id") installationId: Long
    ): InstallationResponse

    @PostMapping("/login/oauth/access_token")
    @Headers("Accept: application/json")
    fun getAccessToken(
        @RequestParam("client_id") clientId: String,
        @RequestParam("client_secret") clientSecret: String,
        @RequestParam("code") code: String
    ): GithubAccessTokenResponse

    @PostMapping("/repos/{owner}/{repo}/git/refs")
    @Headers("Accept: application/vnd.github+json")
    fun createRef(
        @RequestHeader("Authorization") token: String,
        @PathVariable("owner") owner: String,
        @PathVariable("repo") repo: String,
        @RequestBody request: CreateRefRequest
    ): GithubRefResponse

    @PostMapping("/repos/{owner}/{repo}/git/blobs")
    @Headers("Accept: application/vnd.github+json")
    fun createBlob(
        @RequestHeader("Authorization") token: String,
        @PathVariable("owner") owner: String,
        @PathVariable("repo") repo: String,
        @RequestBody request: Map<String, String>
    ): GithubBlobResponse
}

