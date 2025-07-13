
package com.leita.leita.port.github

import com.fasterxml.jackson.databind.JsonNode
import com.leita.leita.common.config.GithubConfig
import com.leita.leita.port.github.dto.GithubCommitRequest
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64
import java.util.Date

@Component
class GithubAdapter(
    private val githubConfig: GithubConfig,
    private val githubClient: GithubClient
) : GithubPort {

    override fun getInstallationAccessToken(installationId: Long): String {
        val jwt = createJwt()
        val response = githubClient.createInstallationAccessToken("Bearer $jwt", installationId)
        return response.token
    }

    override fun getInstallationRepositories(token: String): JsonNode {
        return githubClient.getInstallationRepositories("Bearer $token")
    }

    override fun commitFileToRepository(
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
    ): JsonNode {
        val request = GithubCommitRequest(
            message = message,
            content = content,
            sha = sha,
            branch = branch,
            repo = repo,
            committer = GithubCommitRequest.Committer(authorName, authorEmail)
        )
        return githubClient.commitFileToRepository("Bearer $token", owner, repo, path, request)
    }

    private fun createJwt(): String {
        val nowMillis = System.currentTimeMillis()
        val now = Date(nowMillis)
        val expirationMillis = nowMillis + 10 * 60 * 1000 // 10 minutes

        return Jwts.builder()
            .setIssuer(githubConfig.appId)
            .setIssuedAt(now)
            .setExpiration(Date(expirationMillis))
            .signWith(getPrivateKey(), SignatureAlgorithm.RS256)
            .compact()
    }

    private fun getPrivateKey(): PrivateKey {
        val privateKeyBytes = Base64.getDecoder().decode(githubConfig.privateKey)
        val keySpec = PKCS8EncodedKeySpec(privateKeyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePrivate(keySpec)
    }
}
