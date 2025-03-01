package com.leita.leita.port.submit

import com.leita.leita.common.config.RestConfig
import com.leita.leita.common.exception.CustomException
import com.leita.leita.controller.dto.submit.request.JudgeSubmitRequest
import com.leita.leita.controller.dto.submit.request.RunSubmitRequest
import com.leita.leita.port.submit.dto.request.JudgeSubmitWCRequest
import com.leita.leita.port.submit.dto.request.RunSubmitWCRequest
import com.leita.leita.port.submit.dto.response.JudgeSubmitWCResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class SubmitAdapter(
    private val restConfig: RestConfig,
    private val webClient: WebClient
) : SubmitPort {

    @Async
    override fun judgeSubmit(problemId: Long, submitId: Long, request: JudgeSubmitRequest): Boolean {
        try {
            val judgeRequest = JudgeSubmitWCRequest(
                submitId,
                code = request.code,
                language = request.language,
            )
            println(judgeRequest)

            val response = webClient.post()
                .uri(request.language.getUrl(restConfig.judge) + "/judge/" + problemId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(judgeRequest)
                .retrieve()
                .bodyToMono(JudgeSubmitWCResponse::class.java)
                .block()!!

            println("Judge server responded: ${request.language.getUrl(restConfig.judge) + "/judge/" + problemId} / $response")
            return response.isSuccessful
        } catch (ex: Exception) {
            println(ex.message)
            throw CustomException("제출 실패", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Async
    override fun runSubmit(problemId: Long, submitId: Long, request: RunSubmitRequest): Boolean {
        try {
            val judgeRequest = RunSubmitWCRequest(
                code = request.code,
                language = request.language,
                testCases = request.testCases
            )
            println(judgeRequest)

            val response = webClient.post()
                .uri(request.language.getUrl(restConfig.judge) + "/run/" + problemId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(judgeRequest)
                .retrieve()
                .bodyToMono(JudgeSubmitWCResponse::class.java)
                .block()!!

            println("Judge server responded: ${request.language.getUrl(restConfig.judge) + "/run/" + problemId} / $response")
            return response.isSuccessful
        } catch (ex: Exception) {
            println(ex.message)
            throw CustomException("제출 실패", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}