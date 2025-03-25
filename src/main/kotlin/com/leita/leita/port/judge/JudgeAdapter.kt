package com.leita.leita.port.judge

import com.leita.leita.common.config.RestConfig
import com.leita.leita.common.exception.CustomException
import com.leita.leita.controller.dto.judge.request.SubmitRequest
import com.leita.leita.controller.dto.judge.request.RunRequest
import com.leita.leita.port.judge.dto.request.SubmitWCRequest
import com.leita.leita.port.judge.dto.request.RunWCRequest
import com.leita.leita.port.judge.dto.response.JudgeWCResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class JudgeAdapter(
    private val restConfig: RestConfig,
    private val webClient: WebClient
) : JudgePort {

    @Async
    override fun submit(problemId: Long, submitId: Long, request: SubmitRequest): JudgeWCResponse {
        try {
            val submitRequest = SubmitWCRequest(
                submitId,
                code = request.code,
                language = request.language,
            )
            println(submitRequest)

            return webClient.post()
                .uri(request.language.getUrl(restConfig.judge) + "/problem/submit/" + problemId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(submitRequest)
                .retrieve()
                .bodyToMono<JudgeWCResponse>()
                .doOnSuccess {
                    println("Judge server responded: ${request.language.getUrl(restConfig.judge) + "/problem/submit/" + problemId} / $it")
                }
                .block()!!
        } catch (ex: Exception) {
            println(ex.message)
            throw CustomException("제출 실패", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Async
    override fun run(problemId: Long, submitId: Long, request: RunRequest): List<JudgeWCResponse> {
        try {
            val runRequest = RunWCRequest(
                code = request.code,
                language = request.language,
                testCases = request.testCases,
            )

            return webClient.post()
                .uri(request.language.getUrl(restConfig.judge) + "/problem/run/" + problemId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(runRequest)
                .retrieve()
                .bodyToMono<List<JudgeWCResponse>>()
                .doOnSuccess {
                    println("Judge server responded: ${request.language.getUrl(restConfig.judge) + "/problem/run/" + problemId} / $it")
                }
                .block()!!
        } catch (ex: Exception) {
            println(ex.message)
            throw CustomException("제출 실패", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}