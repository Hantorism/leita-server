package com.leita.leita.common

import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.io.IOException

@Component
class LoggingFilter : Filter {

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val wrappedRequest = if (request is HttpServletRequest) ContentCachingRequestWrapper(request) else request
        val wrappedResponse = if (response is HttpServletResponse) ContentCachingResponseWrapper(response) else response

        chain.doFilter(wrappedRequest, wrappedResponse)

        if (wrappedResponse is ContentCachingResponseWrapper) {
            wrappedResponse.copyBodyToResponse()
        }
    }
}