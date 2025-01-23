package com.leita.leita.port.judge

import com.leita.leita.common.config.RestConfig
import com.leita.leita.controller.dto.auth.request.SubmitRequest
import com.leita.leita.controller.dto.auth.request.SubmitResponse
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@Component
class JudgeAdapter(
    private val restTemplate: RestTemplate,
    private val restConfig: RestConfig
) : JudgePort {

    @Async
    override fun submit(request: SubmitRequest): Boolean {
        try {
            val response = restTemplate.postForObject(
                restConfig.judge + "/judge",
                request,
                SubmitResponse::class.java
            )

            println("Judge server responded: $response")
            
            if (response != null) {
                return response.isSubmit
            }
        } catch (ex: HttpClientErrorException) {
            println("Error while submitting to Judge server: ${ex.statusCode} - ${ex.responseBodyAsString}")
        } catch (ex: Exception) {
            println("Unexpected error: ${ex.message}")
        }
        return false
    }
}