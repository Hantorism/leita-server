package com.leita.leita.port.gmail

import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class GmailAdapter(
    private val mailSender: JavaMailSenderImpl,
) : GmailPort {

    @Async
    override fun send(email: String, code: String) {
        val message: MimeMessage = mailSender.createMimeMessage()

        //  TODO: 메일 필요가 늘어나면 enum으로 타입화
        try {
            val sender = InternetAddress("leita@leita.kr", "LEITA", "utf-8")
            message.setFrom(sender)
            message.setRecipients(MimeMessage.RecipientType.TO, email)
            message.subject = "[LEITA] 이메일 인증"
            message.setText("인증 코드: $code")
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

        mailSender.send(message)
    }
}