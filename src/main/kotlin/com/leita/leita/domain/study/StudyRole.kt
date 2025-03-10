package com.leita.leita.domain.study

import lombok.Getter

@Getter
enum class StudyRole(val code: String) {
    ADMIN("admin"),
    MEMBER("member");
}