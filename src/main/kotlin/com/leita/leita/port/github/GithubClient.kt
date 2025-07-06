package com.leita.leita.port.github
import com.leita.leita.port.github.dto.GithubCommitRequest
import com.leita.leita.port.github.dto.GithubFileResponse
import com.leita.leita.port.github.dto.GithubRepositoryResponse
import com.leita.leita.port.github.dto.GithubUserResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*

@FeignClient(name = "githubClient", url = "\${github.api.url}")
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
}