package com.leita.leita.external.judge

import com.leita.leita.judge.dto.JudgeWCResponse
import com.leita.leita.judge.dto.RunWCRequest
import com.leita.leita.judge.dto.RunWCResponse
import com.leita.leita.judge.dto.SubmitWCRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.net.URI

@FeignClient(name = "judgeClient")
interface JudgeClient {

    @PostMapping
    fun submit(
        baseUri: URI,
        @RequestBody request: SubmitWCRequest
    ): JudgeWCResponse

    @PostMapping
    fun run(
        baseUri: URI,
        @RequestBody request: RunWCRequest
    ): List<RunWCResponse>
}
