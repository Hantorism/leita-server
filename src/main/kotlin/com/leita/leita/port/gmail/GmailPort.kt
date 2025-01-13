package com.leita.leita.port.gmail

interface GmailPort {
    fun send(email: String, code: String)
}