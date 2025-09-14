package com.leita.leita.service

import com.leita.leita.common.exception.CustomException
import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.controller.git.request.CommitRequest
import com.leita.leita.controller.git.response.RepositoryResponse
import com.leita.leita.port.cache.CachePort
import com.leita.leita.port.github.GithubPort
import com.leita.leita.port.github.dto.response.InstallationRepositoriesResponse
import com.leita.leita.repository.JudgeRepository
import com.leita.leita.repository.ProblemRepository
import com.leita.leita.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.Base64

@Service
class GitService(
    private val judgeRepository: JudgeRepository,
    private val githubAppPort: GithubPort,
    private val jwtUtils: JwtUtils,
    private val userRepository: UserRepository,
    private val problemRepository: ProblemRepository,
    private val cachePort: CachePort,
) {
    fun callbackInstall(installationId: Long, state: String) {
        val userId = cachePort.get(state)
            ?: throw CustomException("Callback info not found", HttpStatus.BAD_REQUEST)
        val user = userRepository.findById(userId.toLong())
            .orElseThrow{ throw CustomException("User not found", HttpStatus.NOT_FOUND) }

        user.addGithubApps(installationId)
        userRepository.save(user)
    }

    fun commit(request: CommitRequest) {
        val user = jwtUtils.extractUser()
        val judge = judgeRepository.findById(request.judgeId)
            .orElseThrow{ throw CustomException("Judge not found", HttpStatus.NOT_FOUND) }
        val problem = problemRepository.findById(judge.problemId)
            .orElseThrow{ throw CustomException("Problem not found", HttpStatus.NOT_FOUND) }

        val installationId = user.githubInfo!!.installationId

        val installationAccessToken = githubAppPort.getInstallationAccessToken(installationId)

        val fileName = "[${problem.problemId}] ${problem.title}.md"
        val content = "# ${problem.title} ${problem.description.problem}"
        val encodedContent = Base64.getEncoder().encodeToString(content.toByteArray())

        githubAppPort.commitFileToRepository(
            token = installationAccessToken,
            owner = "Leita",
            repo = request.repositoryName,
            path = fileName,
            message = "Solve: ${problem.title}",
            content = encodedContent,
            sha = null,
            branch = "main",
            authorName = user.name,
            authorEmail = user.email
        )
    }

    fun getRepositories(): List<RepositoryResponse> {
        val user = jwtUtils.extractUser()
        if(user.githubInfo == null) {
            throw CustomException("Github Apps 조회 실패", HttpStatus.BAD_REQUEST)
        }
        val installationId = user.githubInfo!!.installationId

        try {
            val installationAccessToken = githubAppPort.getInstallationAccessToken(installationId)
            val response: InstallationRepositoriesResponse = githubAppPort.getInstallationRepositories(installationAccessToken)
            return response.repositories.map { RepositoryResponse(it.name, it.html_url) }
        } catch (_: Exception) {
            throw CustomException("Repositories not found", HttpStatus.BAD_REQUEST)
        }
    }

    fun createInstallationUrl(): String {
        val baseUrl = "https://github.com/apps/leita-ajou/installations/new"

        val user = jwtUtils.extractUser()
        val state = user.generateGitState()

        // TODO: expire가 있는 cache set으로 변경 필요
        cachePort.set(state.toString(), user.id.toString())

        return "$baseUrl?state=$state"
    }
}