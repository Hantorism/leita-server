package com.leita.leita.service

import com.leita.leita.common.exception.CustomException
import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.port.github.GithubPort
import com.leita.leita.repository.ProblemRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.Base64

@Service
class AutoCommitService(
    private val problemRepository: ProblemRepository,
    private val githubAppPort: GithubPort,
    private val jwtUtils: JwtUtils,
) {
    fun commit(problemId: Long, repositoryName: String) {
        val user = jwtUtils.extractUser()
        val problem = problemRepository.findProblemByProblemId(problemId)
            ?: throw CustomException("Problem not found", HttpStatus.NOT_FOUND)

        val installationId = user.installationId
            ?: throw CustomException("GitHub App not installed", HttpStatus.BAD_REQUEST)

        val installationAccessToken = githubAppPort.getInstallationAccessToken(installationId)

        val fileName = "[${problem.problemId}] ${problem.title}.md"
        val content = "# ${problem.title} ${problem.description.problem}"
        val encodedContent = Base64.getEncoder().encodeToString(content.toByteArray())

        githubAppPort.commitFileToRepository(
            token = installationAccessToken,
            owner = user.githubInfo?.userName ?: throw CustomException("Github user not found", HttpStatus.BAD_REQUEST),
            repo = repositoryName,
            path = fileName,
            message = "Solve: ${problem.title}",
            content = encodedContent,
            sha = null,
            branch = "main",
            authorName = user.name,
            authorEmail = user.email
        )
    }
}