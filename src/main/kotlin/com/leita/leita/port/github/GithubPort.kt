
package com.leita.leita.port.github

import com.fasterxml.jackson.databind.JsonNode
import com.leita.leita.port.github.dto.response.InstallationRepositoriesResponse

interface GithubPort {
    fun getInstallationAccessToken(installationId: Long): String
    fun getInstallationRepositories(token: String): InstallationRepositoriesResponse
    fun commitFileToRepository(
        token: String,
        owner: String,
        repo: String,
        path: String,
        message: String,
        content: String,
        sha: String?,
        branch: String?,
        authorName: String?,
        authorEmail: String?
    ): JsonNode
}
