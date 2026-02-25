package com.leita.leita.util.github.dto.response

data class InstallationResponse(
    val account: Account
) {
    data class Account(
        val login: String
    )
}