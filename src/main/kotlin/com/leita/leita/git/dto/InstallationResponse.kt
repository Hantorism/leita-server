package com.leita.leita.git.dto

data class InstallationResponse(
    val account: Account
) {
    data class Account(
        val login: String
    )
}