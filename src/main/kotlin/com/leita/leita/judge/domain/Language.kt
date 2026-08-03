package com.leita.leita.judge.domain

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
    SWIFT("swift"),
    CS("cs"),
    TYPESCRIPT("typescript"),
    RUST("rust");

    fun getUrl(baseUrl: String): String {
        return baseUrl.replace("{LANGUAGE}", this.code)
    }

    fun toExtension(): String {
        return when (this) {
            C -> "c"
            CPP -> "cpp"
            JAVA -> "java"
            PYTHON -> "py"
            JAVASCRIPT -> "js"
            GO -> "go"
            KOTLIN -> "kt"
            SWIFT -> "swift"
            CS -> "cs"
            TYPESCRIPT -> "ts"
            RUST -> "rs"
        }
    }
}