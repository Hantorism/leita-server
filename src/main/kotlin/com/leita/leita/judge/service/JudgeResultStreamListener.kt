package com.leita.leita.judge.service

import com.leita.leita.judge.domain.Result
import com.leita.leita.judge.dto.JudgeWCResponse
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.stream.StreamListener
import org.springframework.stereotype.Component

@Component
class JudgeResultStreamListener(
    private val judgeService: JudgeService,
    private val stringRedisTemplate: StringRedisTemplate
) : StreamListener<String, MapRecord<String, String, String>> {

    override fun onMessage(record: MapRecord<String, String, String>) {
        try {
            val valueMap = record.value
            val submitIdStr = valueMap["submitId"] ?: return
            val submitId = submitIdStr.toLong()

            val resultStr = valueMap["result"] ?: return
            val error = valueMap["error"] ?: ""
            val usedMemory = valueMap["usedMemory"]?.toLong() ?: 0L
            val usedTime = valueMap["usedTime"]?.toLong() ?: 0L

            val response = JudgeWCResponse(
                result = Result.valueOf(resultStr),
                error = error,
                usedMemory = usedMemory,
                usedTime = usedTime
            )

            judgeService.completeJudge(submitId, response)

            // Acknowledge the message to avoid redelivery
            stringRedisTemplate.opsForStream<String, Any>().acknowledge(
                record.stream!!,
                "leita-app-group",
                record.id
            )
        } catch (e: Exception) {
            println("Error processing Redis Stream message: ${e.message}")
        }
    }
}
