package com.leita.leita.external.judge

import com.leita.leita.common.config.WebClientConfig
import com.leita.leita.common.exception.CustomException
import com.leita.leita.judge.dto.SubmitRequest
import com.leita.leita.judge.dto.RunRequest
import com.leita.leita.judge.dto.SubmitWCRequest
import com.leita.leita.judge.dto.RunWCRequest
import com.leita.leita.judge.dto.JudgeWCResponse
import com.leita.leita.judge.dto.RunWCResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class JudgeUtil(
    private val webClient: WebClient,
    private val webClientConfig: WebClientConfig
) {

    @Async
    fun submit(problemId: Long, submitId: Long, request: SubmitRequest): JudgeWCResponse {
        try {
            val submitRequest = SubmitWCRequest(
                submitId,
                code = request.code,
                language = request.language,
            )

            return webClient.post()
                .uri(request.language.getUrl(webClientConfig.judgeBaseUrl) + "/problem/submit/" + problemId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(submitRequest)
                .retrieve()
                .bodyToMono(JudgeWCResponse::class.java)
                .doOnSuccess {
                    println("Judge server responded: ${request.language.getUrl(webClientConfig.judgeBaseUrl) + "/problem/submit/" + problemId} / $it")
                }
                .block()!!
        } catch (ex: Exception) {
            println(ex.message)
            throw CustomException("제출 실패", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Async
    fun run(problemId: Long, submitId: Long, request: RunRequest): List<RunWCResponse> {
        try {
            val runRequest = RunWCRequest(
                code = request.code,
                language = request.language,
                testCases = request.testCases,
            )

            return webClient.post()
                .uri(request.language.getUrl(webClientConfig.judgeBaseUrl) + "/problem/run/" + problemId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(runRequest)
                .retrieve()
                .bodyToMono(Array<RunWCResponse>::class.java)
                .map { it.toList() }
                .doOnSuccess {
                    println("Judge server responded: ${request.language.getUrl(webClientConfig.judgeBaseUrl) + "/problem/run/" + problemId} / $it")
                }
                .block()!!
        } catch (ex: Exception) {
            println(ex.message)
            throw CustomException("제출 실패", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
