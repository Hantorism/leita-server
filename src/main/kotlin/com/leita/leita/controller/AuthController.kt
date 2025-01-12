package com.leita.leita.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController() {

    @GetMapping
    fun test(): String {
        return "hello world"
    }
}