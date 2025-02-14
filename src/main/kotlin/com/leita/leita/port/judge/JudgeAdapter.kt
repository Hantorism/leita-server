package com.leita.leita.port.judge

import com.leita.leita.common.config.RestConfig
import com.leita.leita.controller.dto.problem.request.SubmitRequest
import com.leita.leita.port.judge.dto.request.JudgeSubmitRequest
import com.leita.leita.port.judge.dto.response.JudgeSubmitResponse
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.reactive.function.client.WebClient

@Component
class JudgeAdapter(
    private val restConfig: RestConfig,
    private val webClient: WebClient
) : JudgePort {

    @Async
    override fun submit(problemId: Long, submitId: Long,  request: SubmitRequest): Boolean {
        try {
            val judgeRequest = JudgeSubmitRequest(
                submitId,
                code = request.code,
                language = request.language,
            )
            println(judgeRequest)
            val response = webClient.post()
                .uri(request.language.getUrl(restConfig.judge) + "/problem/" + problemId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(judgeRequest)
                .retrieve()
                .bodyToMono(JudgeSubmitResponse::class.java)
                .block()!!

            println("Judge server responded: ${request.language.getUrl(restConfig.judge) + "/problem/" + problemId} / $response")
            return response.isSuccessful
        } catch (ex: HttpClientErrorException) {
            println("Error while submitting to Judge server: ${ex.statusCode} - ${ex.responseBodyAsString}")
        } catch (ex: Exception) {
            println("Unexpected error: ${ex.message}")
        }
        return false
    }
}