package com.leita.leita.port.mail

import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class MailAdapter(
    private val mailSender: JavaMailSender,
) : MailPort {

    @Async
    override fun send(mailType: MailType, email: String, vararg args: String) {
        val message: MimeMessage = mailSender.createMimeMessage()

        try {
            val sender = InternetAddress("leita@leita.kr", "LEITA", "utf-8")
            message.setFrom(sender)
            message.setRecipients(MimeMessage.RecipientType.TO, email)

            message.subject = mailType.title
            message.setText(mailType.formatMessage(*args))

        } catch (e: Exception) {
            throw RuntimeException("메일 전송 실패: ${e.message}", e)
        }

        mailSender.send(message)
    }
}