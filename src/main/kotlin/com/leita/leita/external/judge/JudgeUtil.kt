package com.leita.leita.external.judge

import com.leita.leita.common.config.WebClientConfig
import com.leita.leita.common.exception.CustomException
import com.leita.leita.external.judge.messagequeue.RabbitMQConfig
import com.leita.leita.judge.dto.RunRequest
import com.leita.leita.judge.dto.RunWCRequest
import com.leita.leita.judge.dto.RunWCResponse
import com.leita.leita.judge.dto.SubmitRequest
import com.leita.leita.judge.dto.SubmitWCRequest
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.net.URI

@Component
class JudgeUtil(
    private val judgeClient: JudgeClient,
    private val webClientConfig: WebClientConfig,
    private val rabbitTemplate: RabbitTemplate
) {

    /**
     * MQ를 통한 비동기 채점 요청 발행
     */
    fun submitAsync(problemId: Long, submitId: Long, request: SubmitRequest) {
        try {
            val submitRequest = SubmitWCRequest(
                submitId = submitId,
                code = request.code,
                language = request.language,
            )
            
            // 실제 채점 서버로 가는 URL 정보를 포함하여 MQ에 발행 (채점 서버가 이 정보를 참고하거나, 고정된 큐를 구독)
            // 여기서는 단순화하여 요청 객체 자체를 발행
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.JUDGE_EXCHANGE,
                RabbitMQConfig.JUDGE_REQUEST_ROUTING_KEY,
                submitRequest
            )
        } catch (ex: Exception) {
            println("MQ 발행 실패: ${ex.message}")
            throw CustomException("채점 요청 실패 (MQ)", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    /**
     * FeignClient를 통한 동기식 실행 (테스트 케이스 실행용)
     */
    fun run(problemId: Long, submitId: Long, request: RunRequest): List<RunWCResponse> {
        try {
            val runRequest = RunWCRequest(
                code = request.code,
                language = request.language,
                testCases = request.testCases,
            )

            val url = request.language.getUrl(webClientConfig.judgeBaseUrl) + "/problem/run/" + problemId
            return judgeClient.run(URI(url), runRequest)
        } catch (ex: Exception) {
            println("Feign 호출 실패: ${ex.message}")
            throw CustomException("실행 실패 (Feign)", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
