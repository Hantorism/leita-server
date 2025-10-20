package com.leita.leita.port.github

import com.leita.leita.port.github.dto.response.CreateCommitResponse
import com.leita.leita.port.github.dto.response.InstallationRepositoriesResponse

interface GithubPort {
    fun getInstallationRepositories(installationId: Long): InstallationRepositoriesResponse
    fun getInstallation(installationId: Long, code: String): String
    fun commitMultipleFiles(
        installationId: Long,
        owner: String,
        repo: String,
        branch: String,
        message: String,
        files: Map<String, String>,
        authorName: String?,
        authorEmail: String?
    ): CreateCommitResponse
}
