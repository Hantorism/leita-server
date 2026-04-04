package com.leita.leita.external.judge.messagequeue

import com.leita.leita.judge.dto.JudgeWCResponse
import com.leita.leita.judge.service.JudgeService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class JudgeResultConsumer(
    private val judgeService: JudgeService
) {

    @RabbitListener(queues = [RabbitMQConfig.JUDGE_RESULT_QUEUE])
    fun consumeJudgeResult(response: JudgeWCResponse) {
        println("MQ로부터 채점 결과 수신: judgeId=${response.submitId}, result=${response.result}")
        try {
            judgeService.processJudgeResult(response)
        } catch (e: Exception) {
            println("채점 결과 처리 중 오류 발생: ${e.message}")
            // 필요 시 nack 또는 DLQ 처리를 고려할 수 있음
        }
    }
}
