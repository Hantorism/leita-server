package com.leita.leita.service

import com.leita.leita.common.exception.CustomException
import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.controller.dto.judge.request.ReviewRequest
import com.leita.leita.controller.git.response.RepositoryResponse
import com.leita.leita.util.cache.CacheUtil
import com.leita.leita.util.github.GithubUtil
import com.leita.leita.util.github.dto.response.InstallationRepositoriesResponse
import com.leita.leita.util.storage.OracleStorageUtil
import com.leita.leita.repository.JudgeRepository
import com.leita.leita.repository.ProblemRepository
import com.leita.leita.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.Base64

@Service
class GitService(
    private val judgeRepository: JudgeRepository,
    private val githubUtil: GithubUtil,
    private val jwtUtils: JwtUtils,
    private val userRepository: UserRepository,
    private val problemRepository: ProblemRepository,
    private val cacheUtil: CacheUtil,
    private val oracleStorageUtil: OracleStorageUtil,
) {
    fun callbackInstall(installationId: Long, state: String, code: String) {
        val userId = cacheUtil.get(state)
            ?: throw CustomException("Callback info not found", HttpStatus.BAD_REQUEST)
        val user = userRepository.findById(userId.toLong())
            .orElseThrow{ throw CustomException("User not found", HttpStatus.NOT_FOUND) }

        val githubUserName = githubUtil.getInstallation(installationId, code)

        user.addGithubApps(installationId, githubUserName)
        userRepository.save(user)
    }

    fun getRepositories(): List<RepositoryResponse> {
        val user = jwtUtils.extractUser()
        if(user.githubInfo == null) {
            throw CustomException("Github Apps 조회 실패", HttpStatus.BAD_REQUEST)
        }
        val installationId = user.githubInfo!!.installationId

        try {
            val response: InstallationRepositoriesResponse = githubUtil.getInstallationRepositories(installationId)
            return response.repositories.map { RepositoryResponse(it.name, it.html_url) }
        } catch (_: Exception) {
            throw CustomException("Repositories not found", HttpStatus.BAD_REQUEST)
        }
    }

    fun createInstallationUrl(): String {
        val user = jwtUtils.extractUser()
        val state = user.generateGitState()

        // TODO: expire가 있는 cache set으로 변경 필요
        cacheUtil.set(state.toString(), user.id.toString())

        return "https://github.com/apps/leita-ajou/installations/new?state=$state"
    }

    fun autoCommit(request: ReviewRequest) {
        val user = jwtUtils.extractUser()
        val judge = judgeRepository.findById(request.submitId)
            .orElseThrow{ throw CustomException("Judge not found", HttpStatus.NOT_FOUND) }
        val problem = problemRepository.findById(judge.problemId)
            .orElseThrow{ throw CustomException("Problem not found", HttpStatus.NOT_FOUND) }

        val installationId = user.githubInfo!!.installationId
        val githubUserName = user.githubInfo!!.githubUserName

        val reviewContent = """
            # [${problem.problemId}] ${problem.title}

            **문제 링크**: [${problem.title}](https://leita.dev/problems/${problem.problemId})
            
            **제출 언어**: ${judge.used?.language ?: ""}
            
            **결과**: ${judge.result?.message}
            
            **메모리 사용량**: ${judge.used?.memory ?: 0} KB
            
            **실행 시간**: ${judge.used?.time ?: 0} ms

            ```
            ${request.description}
            ```
        """.trimIndent()

        val codeEncodedContent = oracleStorageUtil.downloadFile("submits/${judge.id}/Main.${judge.used?.language?.toExtension()}")
        val codeContent = Base64.getDecoder().decode(codeEncodedContent)

        val reviewFileName = "${problem.problemId}/review.md"
        val codeFileName = "${problem.problemId}/${problem.problemId}.${judge.used?.language?.toExtension()}"

        val filesToCommit = mapOf(
            reviewFileName to reviewContent,
            codeFileName to String(codeContent)
        )

        githubUtil.commitMultipleFiles(
            installationId,
            owner = githubUserName,
            repo = request.repositoryName,
            branch = "main",
            message = "Solve: ${problem.title}",
            files = filesToCommit,
            authorName = user.name,
            authorEmail = user.email
        )
    }
}