package com.leita.leita.common.config

import com.leita.leita.judge.service.JudgeResultStreamListener
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.stream.Consumer
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.connection.stream.ReadOffset
import org.springframework.data.redis.connection.stream.StreamOffset
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.data.redis.stream.StreamMessageListenerContainer
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions
import java.time.Duration

@Configuration
class RedisConfig(
    @Value("\${leita.redis.judge-stream:judge-result-stream}")
    private val streamKey: String
) {

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = connectionFactory
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = StringRedisSerializer()
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = StringRedisSerializer()
        return template
    }

    @Bean(destroyMethod = "stop")
    fun streamMessageListenerContainer(
        connectionFactory: RedisConnectionFactory,
        judgeResultStreamListener: JudgeResultStreamListener
    ): StreamMessageListenerContainer<String, MapRecord<String, String, String>> {
        val groupName = "leita-app-group"

        try {
            // makeStream = true creates the stream automatically if it doesn't exist
            connectionFactory.connection.streamCommands().xGroupCreate(
                streamKey.toByteArray(),
                groupName,
                ReadOffset.latest(),
                true
            )
        } catch (e: Exception) {
            // Already exists or creation failed
            println("Redis Stream consumer group creation message: ${e.message}")
        }

        val options = StreamMessageListenerContainerOptions.builder()
            .pollTimeout(Duration.ofSeconds(1))
            .build()

        val container = StreamMessageListenerContainer.create(connectionFactory, options)

        container.receive(
            Consumer.from(groupName, "app-instance-1"),
            StreamOffset.create(streamKey, ReadOffset.lastConsumed()),
            judgeResultStreamListener
        )

        container.start()
        return container
    }
}
