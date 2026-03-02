package com.leita.leita.util.github

import com.leita.leita.common.config.GithubConfig
import com.leita.leita.common.lib.PrivateKeyParser
import com.leita.leita.util.github.dto.request.*
import com.leita.leita.util.github.dto.response.CreateCommitResponse
import com.leita.leita.util.github.dto.response.InstallationRepositoriesResponse
import com.leita.leita.util.github.dto.response.InstallationResponse
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import java.util.*

@Component
class GithubUtil(
    private val githubConfig: GithubConfig,
    private val githubClient: GithubClient
) {

    fun getInstallationRepositories(installationId: Long): InstallationRepositoriesResponse {
        val token = getInstallationAccessToken(installationId)
        return githubClient.getInstallationRepositories("Bearer $token")
    }

    fun getInstallation(installationId: Long, code: String): String {
        val jwt = createJwt()
        val response: InstallationResponse = githubClient.getInstallation("Bearer $jwt", installationId)
        return response.account.login
    }

    fun commitMultipleFiles(
        installationId: Long, owner: String, repo: String, branch: String, message: String,
        files: Map<String, String>, authorName: String?, authorEmail: String?
    ): CreateCommitResponse {
        val token = getInstallationAccessToken(installationId)

        val refExists = try {
            githubClient.getRef("Bearer $token", owner, repo, branch)
            true
        } catch (_: Exception) {
            false
        }

        if (!refExists) {
            val firstFileEntry = files.entries.first()
            val firstFilePath = firstFileEntry.key
            val firstFileContent = firstFileEntry.value

            githubClient.commitFileToRepository(
                "Bearer $token",
                owner,
                repo,
                firstFilePath,
                GithubCommitRequest(
                    message = message.ifBlank { "Initial commit" },
                    content = Base64.getEncoder().encodeToString(firstFileContent.toByteArray()),
                    repo = repo,
                )
            )

            if (files.size == 1) {
                val ref = githubClient.getRef("Bearer $token", owner, repo, branch)
                val commitSha = ref.`object`.sha
                return CreateCommitResponse(
                    sha = commitSha,
                    url = ref.url
                )
            }
        }

        val ref = githubClient.getRef("Bearer $token", owner, repo, branch)
        val parentCommitSha = ref.`object`.sha

        val blobs = files.map { (path, content) ->
            val blobRequest = mapOf("content" to content, "encoding" to "utf-8")
            val blob = githubClient.createBlob("Bearer $token", owner, repo, blobRequest)
            TreeObject(path = path, sha = blob.sha)
        }

        val treeRequest = GithubTreeRequest(tree = blobs)
        val tree = githubClient.createTree("Bearer $token", owner, repo, treeRequest)

        val commitRequest = CreateCommitRequest(
            message = message,
            tree = tree.sha,
            parents = listOf(parentCommitSha),
            author = if (authorName != null && authorEmail != null)
                Author(authorName, authorEmail)
            else null
        )
        val commit = githubClient.createCommit("Bearer $token", owner, repo, commitRequest)

        githubClient.updateRef(
            "Bearer $token",
            owner,
            repo,
            branch,
            UpdateRefRequest(sha = commit.sha)
        )

        return commit
    }

    fun getInstallationAccessToken(installationId: Long): String {
        val jwt = createJwt()
        val response = githubClient.createInstallationAccessToken("Bearer $jwt", installationId)
        return response.token
    }

    fun createJwt(): String {
        val nowMillis = System.currentTimeMillis()
        val now = Date(nowMillis)
        val expirationMillis = nowMillis + 10 * 60 * 1000

        return Jwts.builder()
            .setIssuer(githubConfig.appId)
            .setIssuedAt(now)
            .setExpiration(Date(expirationMillis))
            .signWith(PrivateKeyParser.parsePrivateKey(githubConfig.privateKey), SignatureAlgorithm.RS256)
            .compact()
    }
}
