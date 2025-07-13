
package com.leita.leita.port.github

import com.fasterxml.jackson.databind.JsonNode

interface GithubPort {
    fun getInstallationAccessToken(installationId: Long): String
    fun getInstallationRepositories(token: String): JsonNode
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
