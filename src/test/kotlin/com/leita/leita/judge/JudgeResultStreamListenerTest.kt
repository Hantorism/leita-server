package com.leita.leita.judge

import com.leita.leita.judge.domain.Result
import com.leita.leita.judge.dto.JudgeWCResponse
import com.leita.leita.judge.service.JudgeResultStreamListener
import com.leita.leita.judge.service.JudgeService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.connection.stream.RecordId
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.StreamOperations

@DisplayName("JudgeResultStreamListener 테스트")
class JudgeResultStreamListenerTest {

    private lateinit var judgeService: JudgeService
    private lateinit var stringRedisTemplate: StringRedisTemplate
    private lateinit var listener: JudgeResultStreamListener

    @BeforeEach
    fun setUp() {
        judgeService = mock(JudgeService::class.java)
        stringRedisTemplate = mock(StringRedisTemplate::class.java)
        listener = JudgeResultStreamListener(judgeService, stringRedisTemplate)
    }

    @Test
    fun `Redis Stream으로부터 메시지를 수신하여 채점 완료 처리를 수행하고 ACK를 전송한다`() {
        // given
        val streamKey = "judge-result-stream"
        val recordId = RecordId.of("1620000000000-0")
        
        val valueMap = mapOf(
            "submitId" to "42",
            "result" to "CORRECT",
            "error" to "",
            "usedMemory" to "2048",
            "usedTime" to "150"
        )

        val record = MapRecord.create(streamKey, valueMap).withId(recordId)

        // mock redis operations
        val streamOps = mock(StreamOperations::class.java) as StreamOperations<String, Any, Any>
        `when`(stringRedisTemplate.opsForStream<Any, Any>()).thenReturn(streamOps)

        // when
        listener.onMessage(record)

        // then
        val expectedResponse = JudgeWCResponse(
            result = Result.CORRECT,
            error = "",
            usedMemory = 2048L,
            usedTime = 150L
        )
        
        // Verify completeJudge was called
        verify(judgeService).completeJudge(42L, expectedResponse)
        
        // Verify acknowledge was called
        verify(streamOps).acknowledge(streamKey, "leita-app-group", recordId)
    }
}
