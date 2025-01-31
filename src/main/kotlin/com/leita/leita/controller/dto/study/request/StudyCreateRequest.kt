package com.leita.leita.controller.dto.study.request

class StudyCreateRequest(
    val adminEmails: MutableList<String>,
    val title: String,
    val description: String,
    val pendingEmails: MutableList<String>,
)
