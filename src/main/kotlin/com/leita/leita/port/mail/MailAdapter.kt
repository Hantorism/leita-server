package com.leita.leita.port.mail

import com.leita.leita.common.exception.CustomException
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import org.springframework.http.HttpStatus
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class MailAdapter(
    private val mailSender: JavaMailSender,
) : MailPort {


    @Async
    override fun send(mailType: MailType, email: String, vararg args: String) {
        try {
            val message = createMessage(mailType, email, *args)
            mailSender.send(message)
        } catch (e: Exception) {
            throw CustomException("메일 전송 실패: ${e.message}", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Async
    override fun sendAll(mailType: MailType, emails: List<String>, vararg args: String) {
        emails.forEach { email ->
            CompletableFuture.runAsync {
                try {
                    val message = createMessage(mailType, email, *args)
                    mailSender.send(message)
                } catch (e: Exception) {
                    throw CustomException("메일 전송 실패: ${e.message}", HttpStatus.INTERNAL_SERVER_ERROR)
                }
            }
        }
    }

    private fun createMessage(mailType: MailType, email: String, vararg args: String): MimeMessage {
        val message: MimeMessage = mailSender.createMimeMessage()
        val sender = InternetAddress("leita@leita.kr", "LEITA", "utf-8")

        message.setFrom(sender)
        message.setRecipients(MimeMessage.RecipientType.TO, email)
        message.subject = mailType.title
        message.setText(mailType.formatMessage(*args))

        return message
    }
}