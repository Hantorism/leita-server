package com.leita.leita.common.security

object ApiPaths {
    val AUTHENTICATED_ENDPOINTS: Array<String> = arrayOf(
        "/auth/info",
    )

    val SWAGGER_ENDPOINTS: Array<String> = arrayOf(
        "/swagger-resources/**",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/v3/api-docs",
    )
}
