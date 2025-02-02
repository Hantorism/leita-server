package com.leita.leita.port.mail

interface MailPort {
    fun send(mailType: MailType, email: String, vararg args: String)
}