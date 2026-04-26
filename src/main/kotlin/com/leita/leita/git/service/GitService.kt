package com.leita.leita.git.service

import com.leita.leita.common.exception.CustomException
import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.judge.dto.ReviewRequest
import com.leita.leita.git.dto.RepositoryResponse
import com.leita.leita.util.cache.CacheUtil
import com.leita.leita.git.util.GithubUtil
import com.leita.leita.git.dto.InstallationRepositoriesResponse
import com.leita.leita.file.util.OracleStorageUtil
import com.leita.leita.git.util.GithubClient
import com.leita.leita.judge.repository.JudgeRepository
import com.leita.leita.problem.repository.ProblemRepository
import com.leita.leita.user.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.Base64
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class GitService(
    private val judgeRepository: JudgeRepository,
    private val githubUtil: GithubUtil,
    private val githubClient: GithubClient,
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
        val installationId = user.githubInfo!!.installationId!!

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
        val problem = problemRepository.findProblemByProblemId(judge.problemId)
            .let { it ?: throw CustomException("Problem not found", HttpStatus.NOT_FOUND) }

        val installationId = user.githubInfo!!.installationId!!
        val githubUserName = user.githubInfo!!.githubUserName!!

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

        val extension = judge.used?.language?.toExtension()
        val readmeFileName = "${problem.problemId}/README.md"
        val codeFileName = "${problem.problemId}/${problem.problemId}.$extension"

        val filesToCommit = mutableMapOf(
            readmeFileName to reviewContent,
            codeFileName to String(codeContent)
        )

        // 깃허브 연동 정보 및 토큰 준비
        val token = githubUtil.getInstallationAccessToken(installationId)
        
        // 이전 성공 제출 기록 찾기 (현재 제출 제외)
        val previousJudge = judgeRepository.findFirstByUserIdAndProblemIdAndResultAndIdNotOrderByCreatedAtDesc(
            user.id, problem.problemId, com.leita.leita.judge.domain.Result.CORRECT, judge.id
        )

        if (previousJudge != null) {
            // 이전에 풀었던 기록이 있다면, 현재 깃허브 루트에 있는 파일들을 아카이브 폴더로 이동시킴
            try {
                // 1. 기존 README.md 내용 가져오기
                val oldReadmeResponse = githubClient.getContent("Bearer $token", githubUserName, request.repositoryName, readmeFileName) as? Map<*, *>
                val oldReadmeBase64 = oldReadmeResponse?.get("content") as? String
                
                // 2. 기존 소스코드 내용 가져오기
                val oldCodeResponse = githubClient.getContent("Bearer $token", githubUserName, request.repositoryName, codeFileName) as? Map<*, *>
                val oldCodeBase64 = oldCodeResponse?.get("content") as? String

                if (oldReadmeBase64 != null && oldCodeBase64 != null) {
                    val prevTimestamp = previousJudge.createdAt.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                    
                    // 정규표현식으로 줄바꿈 제거 후 디코딩 (GitHub API 응답 특성 대응)
                    val decodedReadme = String(Base64.getMimeDecoder().decode(oldReadmeBase64.replace("\n", "")))
                    val decodedCode = String(Base64.getMimeDecoder().decode(oldCodeBase64.replace("\n", "")))

                    filesToCommit["${problem.problemId}/$prevTimestamp/README.md"] = decodedReadme
                    filesToCommit["${problem.problemId}/$prevTimestamp/${problem.problemId}.$extension"] = decodedCode
                }
            } catch (_: Exception) {
                // 파일이 없거나 에러 발생 시 아카이브 생략 (최초 업로드로 간주)
            }
        }

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
