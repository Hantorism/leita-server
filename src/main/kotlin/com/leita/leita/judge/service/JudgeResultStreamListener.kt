package com.leita.leita.judge.service

import com.leita.leita.judge.domain.Result
import com.leita.leita.judge.dto.JudgeWCResponse
import com.leita.leita.common.exception.CustomException
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.stream.StreamListener
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component
class JudgeResultStreamListener(
    private val judgeService: JudgeService,
    private val stringRedisTemplate: StringRedisTemplate
) : StreamListener<String, MapRecord<String, String, String>> {

    override fun onMessage(record: MapRecord<String, String, String>) {
        val valueMap = record.value
        val submitIdStr = valueMap["submitId"]
        val resultStr = valueMap["result"]

        if (submitIdStr == null || resultStr == null) {
            println("Non-transient error: missing submitId or result. Acknowledging message.")
            ackMessage(record)
            return
        }

        val submitId: Long
        val result: Result
        try {
            submitId = submitIdStr.toLong()
            result = Result.valueOf(resultStr)
        } catch (e: Exception) {
            // NumberFormatException, IllegalArgumentException 등 비일시적 파싱 에러
            println("Non-transient parsing error: ${e.message}. Acknowledging message to avoid block.")
            ackMessage(record)
            return
        }

        val error = valueMap["error"] ?: ""
        val usedMemory = valueMap["usedMemory"]?.toLongOrNull() ?: 0L
        val usedTime = valueMap["usedTime"]?.toLongOrNull() ?: 0L

        val response = JudgeWCResponse(
            result = result,
            error = error,
            usedMemory = usedMemory,
            usedTime = usedTime
        )

        try {
            judgeService.completeJudge(submitId, response)
            ackMessage(record)
        } catch (e: CustomException) {
            if (e.status == HttpStatus.NOT_FOUND) {
                println("Non-transient business error (Submit not found): ${e.message}. Acknowledging message.")
                ackMessage(record)
            } else {
                println("Transient business error: ${e.message}. Leaving unacknowledged.")
            }
        } catch (e: Exception) {
            println("Transient system error: ${e.message}. Leaving unacknowledged.")
        }
    }

    private fun ackMessage(record: MapRecord<String, String, String>) {
        try {
            stringRedisTemplate.opsForStream<String, Any>().acknowledge(
                record.stream!!,
                "leita-app-group",
                record.id
            )
        } catch (e: Exception) {
            println("Failed to acknowledge message: ${e.message}")
        }
    }
}
