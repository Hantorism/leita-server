package com.leita.leita.external.judge.messagequeue

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfig {

    companion object {
        const val JUDGE_REQUEST_QUEUE = "judge.request.queue"
        const val JUDGE_RESULT_QUEUE = "judge.result.queue"
        const val JUDGE_EXCHANGE = "judge.exchange"
        const val JUDGE_REQUEST_ROUTING_KEY = "judge.request"
        const val JUDGE_RESULT_ROUTING_KEY = "judge.result"
    }

    @Bean
    fun requestQueue() = Queue(JUDGE_REQUEST_QUEUE)

    @Bean
    fun resultQueue() = Queue(JUDGE_RESULT_QUEUE)

    @Bean
    fun judgeExchange() = DirectExchange(JUDGE_EXCHANGE)

    @Bean
    fun requestBinding(requestQueue: Queue, judgeExchange: DirectExchange): Binding =
        BindingBuilder.bind(requestQueue).to(judgeExchange).with(JUDGE_REQUEST_ROUTING_KEY)

    @Bean
    fun resultBinding(resultQueue: Queue, judgeExchange: DirectExchange): Binding =
        BindingBuilder.bind(resultQueue).to(judgeExchange).with(JUDGE_RESULT_ROUTING_KEY)

    @Bean
    fun jsonMessageConverter() = Jackson2JsonMessageConverter()

    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate {
        val rabbitTemplate = RabbitTemplate(connectionFactory)
        rabbitTemplate.messageConverter = jsonMessageConverter()
        return rabbitTemplate
    }
}
