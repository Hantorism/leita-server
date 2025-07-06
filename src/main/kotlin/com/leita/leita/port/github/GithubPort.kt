package com.leita.leita.port.github

import com.leita.leita.port.github.model.FileExistenceResult
import com.leita.leita.port.github.model.GithubRepository
import com.leita.leita.port.github.model.GithubUserInfo

interface GithubPort {
    fun getUserInfo(accessToken: String): GithubUserInfo
    fun getRepositories(accessToken: String): List<GithubRepository>
    fun commitCode(accessToken: String, repositoryFullName: String, filePath: String, content: String, commitMessage: String)
    fun checkFileExists(accessToken: String, repositoryFullName: String, filePath: String): FileExistenceResult?
}