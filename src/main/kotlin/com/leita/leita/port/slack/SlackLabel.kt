package com.leita.leita.port.slack

enum class SlackLabel(
    val icon: String, val title: String
) {
    SYSTEM_ALERT(":rotating_light:", "에러 발생"),
    SECURITY(":key:", "인증 에러 발생"),
    OTHER(":pencil:", "기타")
}