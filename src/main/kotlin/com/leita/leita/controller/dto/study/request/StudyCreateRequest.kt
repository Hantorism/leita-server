package com.leita.leita.controller.dto.study.request

class StudyCreateRequest(
    val adminEmails: List<String>,
    val title: String,
    val description: String,
    val pendingEmails: List<String>,
)
