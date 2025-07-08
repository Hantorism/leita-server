package com.leita.leita.port.judge

import com.leita.leita.port.judge.dto.request.RunWCRequest
import com.leita.leita.port.judge.dto.request.SubmitWCRequest
import com.leita.leita.port.judge.dto.response.JudgeWCResponse
import com.leita.leita.port.judge.dto.response.RunWCResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping

@FeignClient(name = "judgeClient", url = "\${external-server.judge}")
interface JudgeClient {
    @PostMapping
    fun submit(request: SubmitWCRequest): JudgeWCResponse

    @PostMapping
    fun run(request: RunWCRequest): List<RunWCResponse>
}