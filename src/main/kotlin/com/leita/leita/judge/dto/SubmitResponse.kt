package com.leita.leita.judge.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.leita.leita.judge.domain.Result

data class SubmitResponse(
    @get:JsonProperty("submitId")
    @field:JsonProperty("submitId")
    val submitId: Long,

    @get:JsonProperty("result")
    @field:JsonProperty("result")
    val result: Result?,

    @get:JsonProperty("error")
    @field:JsonProperty("error")
    val error: String?
)
