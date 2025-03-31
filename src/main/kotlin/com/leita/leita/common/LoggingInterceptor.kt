package com.leita.leita.common

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.nio.charset.StandardCharsets

@Component
class LoggingInterceptor : HandlerInterceptor {

    private val log = LoggerFactory.getLogger(LoggingInterceptor::class.java)
    private val objectMapper = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val wrappedRequest = request as? ContentCachingRequestWrapper
        logRequest(wrappedRequest)
        return true
    }

    override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) {
        val wrappedRequest = request as? ContentCachingRequestWrapper
        val wrappedResponse = response as? ContentCachingResponseWrapper
        logResponse(wrappedRequest, wrappedResponse)
        wrappedResponse?.copyBodyToResponse()
    }

    private fun logRequest(request: ContentCachingRequestWrapper?) {
        if (request != null) {
            val requestBody = formatJson(request.contentAsByteArray.toString(StandardCharsets.UTF_8))
            log.info("[Request] {} {} from {}\n{}", request.method, request.requestURI, request.remoteAddr, requestBody)
        }
    }

    private fun logResponse(request: ContentCachingRequestWrapper?, response: ContentCachingResponseWrapper?) {
        if (response != null) {
            val responseBody = formatJson(response.contentAsByteArray.toString(StandardCharsets.UTF_8))
            log.info("[Response] {} {} -> Status: {}\n{}", request?.method, request?.requestURI, response.status, responseBody)
        }
    }

    private fun formatJson(json: String): String {
        return try {
            objectMapper.readTree(json).toPrettyString()
        } catch (e: Exception) {
            json
        }
    }
}