package com.leita.leita.domain.judge

import lombok.Getter

@Getter
enum class Language(val code: String) {
    C("c"),
    CPP("cpp"),
    JAVA("java"),
    PYTHON("python"),
    JAVASCRIPT("javascript"),
    GO("go"),
    KOTLIN("kotlin"),
    SWIFT("swift");

    fun getUrl(baseUrl: String): String {
        return baseUrl.replace("{LANGUAGE}", this.code)
    }
}