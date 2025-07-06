package com.leita.leita.port.github

import com.leita.leita.port.github.dto.GithubCommitRequest
import com.leita.leita.port.github.model.FileExistenceResult
import com.leita.leita.port.github.model.GithubRepository
import com.leita.leita.port.github.model.GithubUserInfo
import feign.FeignException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.Base64

@Component
class GithubAdapter(private val githubClient: GithubClient) : GithubPort {
    private val logger = LoggerFactory.getLogger(GithubAdapter::class.java)

    override fun getUserInfo(accessToken: String): GithubUserInfo {
        val response = githubClient.getUser("Bearer $accessToken")
        return GithubUserInfo(
            id = response.id,
            login = response.login,
            name = response.name,
            email = response.email
        )
    }

    override fun getRepositories(accessToken: String): List<GithubRepository> {
        return githubClient.getRepositories("Bearer $accessToken").map { repo ->
            GithubRepository(
                id = repo.id,
                name = repo.name,
                fullName = repo.full_name,
                description = repo.description,
                url = repo.html_url,
                isPrivate = repo.private
            )
        }
    }

    override fun checkFileExists(accessToken: String, repositoryFullName: String, filePath: String): FileExistenceResult? {
        val (owner, repo) = repositoryFullName.split("/", limit = 2)
        
        return try {
            val response = githubClient.getFileContent("Bearer $accessToken", owner, repo, filePath)
            response?.let { FileExistenceResult(exists = true, sha = it.sha) }
        } catch (e: FeignException) {
            if (e.status() == 404) {
                FileExistenceResult(exists = false, sha = null)
            } else {
                logger.error("GitHub API 오류: ${e.message}")
                null
            }
        }
    }

    override fun commitCode(accessToken: String, repositoryFullName: String, filePath: String, content: String, commitMessage: String) {
        val (owner, repo) = repositoryFullName.split("/", limit = 2)
        
        val fileExistence = checkFileExists(accessToken, repositoryFullName, filePath)
        val encodedContent = Base64.getEncoder().encodeToString(content.toByteArray())
        
        val commitRequest = GithubCommitRequest(
            message = commitMessage,
            content = encodedContent,
            sha = fileExistence?.sha
        )
        
        githubClient.commitFile("Bearer $accessToken", owner, repo, filePath, commitRequest)
    }
}