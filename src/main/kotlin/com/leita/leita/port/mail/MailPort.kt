package com.leita.leita.port.mail

interface MailPort {
    fun send(mailType: MailType, email: String, vararg args: String)
    fun sendAll(mailType: MailType, emails: Collection<String>, vararg args: String)
}