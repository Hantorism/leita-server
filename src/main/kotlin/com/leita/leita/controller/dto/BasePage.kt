package com.leita.leita.controller.dto

interface BasePage<T> {
    val content: List<T>
    val currentPage: Int
    val totalPages: Int
    val totalElements: Long
    val size: Int
}