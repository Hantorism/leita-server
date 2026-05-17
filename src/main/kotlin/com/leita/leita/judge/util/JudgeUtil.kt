package com.leita.leita.judge.util

import com.leita.leita.common.config.WebClientConfig
import com.leita.leita.common.exception.CustomException
import com.leita.leita.judge.dto.SubmitRequest
import com.leita.leita.judge.dto.RunRequest
import com.leita.leita.judge.dto.SubmitWCRequest
import com.leita.leita.judge.dto.RunWCRequest
import com.leita.leita.judge.dto.JudgeWCResponse
import com.leita.leita.judge.dto.RunWCResponse
import com.leita.leita.judge.dto.LimitWCRequest
import com.leita.leita.problem.domain.Limit
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class JudgeUtil(
    private val webClient: WebClient,
    private val webClientConfig: WebClientConfig
) {

    @Async
    fun submit(problemId: String, submitId: Long, request: SubmitRequest, limit: Limit): JudgeWCResponse {
        var baseUrl = webClientConfig.judgeBaseUrl.trim()
        if (baseUrl.isBlank()) {
            throw CustomException("채점 서버 주소가 설정되지 않았습니다.", HttpStatus.INTERNAL_SERVER_ERROR)
        }

        if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
            baseUrl = "http://$baseUrl"
        }

        val targetUri = request.language.getUrl(baseUrl) + "/problem/submit/" + problemId

        try {
            val submitRequest = SubmitWCRequest(
                submitId,
                code = request.code,
                language = request.language,
                limit = LimitWCRequest(limit.memory, limit.time)
            )

            val timeoutSeconds = (limit.time / 1000L) + 60

            return webClient.post()
                .uri(java.net.URI.create(targetUri))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(submitRequest)
                .retrieve()
                .bodyToMono(JudgeWCResponse::class.java)
                .timeout(java.time.Duration.ofSeconds(timeoutSeconds))
                .doOnSuccess {
                    println("Judge server responded: $targetUri / $it")
                }
                .block()!!
        } catch (ex: Exception) {
            println("Error during judge submit to $targetUri: ${ex.message}")
            throw CustomException("제출 실패: ${ex.message}", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Async
    fun run(problemId: String, submitId: Long, request: RunRequest, limit: Limit): List<RunWCResponse> {
        var baseUrl = webClientConfig.judgeBaseUrl.trim()
        if (baseUrl.isBlank()) {
            throw CustomException("채점 서버 주소가 설정되지 않았습니다.", HttpStatus.INTERNAL_SERVER_ERROR)
        }

        if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
            baseUrl = "http://$baseUrl"
        }

        val targetUri = request.language.getUrl(baseUrl) + "/problem/run/" + problemId

        try {
            val runRequest = RunWCRequest(
                code = request.code,
                language = request.language,
                testCases = request.testCases,
                memoryLimit = limit.memory,
                timeLimit = limit.time,
                limit = LimitWCRequest(limit.memory, limit.time)
            )

            val timeoutSeconds = (limit.time / 1000L) + 60

            return webClient.post()
                .uri(java.net.URI.create(targetUri))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(runRequest)
                .retrieve()
                .bodyToMono(Array<RunWCResponse>::class.java)
                .map { it.toList() }
                .timeout(java.time.Duration.ofSeconds(timeoutSeconds))
                .doOnSuccess {
                    println("Judge server responded: $targetUri / $it")
                }
                .block()!!
        } catch (ex: Exception) {
            println("Error during judge run to $targetUri: ${ex.message}")
            throw CustomException("제출 실패: ${ex.message}", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
