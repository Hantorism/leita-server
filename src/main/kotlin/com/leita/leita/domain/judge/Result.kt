package com.leita.leita.domain.judge

enum class Result(val message: String) {
    CORRECT("맞았습니다"),
    WRONG("틀렸습니다"),
    COMPILE_ERROR("컴파일 에러"),
    RUNTIME_ERROR("런타임 에러"),
    TIME_OUT("시간 초과"),
    MEMORY_OUT("메모리 초과"),
    UNKNOWN("기타");
}