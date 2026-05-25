package com.leita.leita.common.security

import lombok.Getter

@Getter
enum class SecurityRole(val key: String) {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN")
}
