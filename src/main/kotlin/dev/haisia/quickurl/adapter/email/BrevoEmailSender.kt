package dev.haisia.quickurl.adapter.email

import dev.haisia.quickurl.application.shared.out.EmailSender
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Component
class BrevoEmailSender(
  private val brevoWebClient: WebClient,
  private val emailTemplateHandler: EmailTemplateHandler
) : EmailSender {

  companion object {
    private val log = LoggerFactory.getLogger(BrevoEmailSender::class.java)
  }

  override fun send(
    recipientEmail: String,
    recipientName: String,
    subject: String,
    htmlContent: String
  ): Mono<String> {
    val textContent = htmlContent
      .replace(Regex("<[^>]*>"), "")
      .replace(Regex("\\s+"), " ")
      .trim()

    val emailRequest = BrevoEmailRequest(
      sender = EmailContact(email = "noreply@haisia.dev", name = "Quickurl"),
      to = listOf(EmailContact(email = recipientEmail, name = recipientName)),
      subject = subject,
      htmlContent = htmlContent,
      textContent = textContent
    )

    return sendEmail(emailRequest)
  }

  override fun sendWelcome(recipientEmail: String, recipientName: String): Mono<String> {
    val content = emailTemplateHandler.renderWelcomeEmail(userName = recipientName, userEmail = recipientEmail)

    return send(
      recipientEmail = recipientEmail,
      recipientName = recipientName,
      subject = "[Quickurl] 회원가입을 환영합니다!",
      htmlContent = content
    )
  }

  override fun sendUrlCreated(
    recipientEmail: String,
    recipientName: String,
    shortUrl: String,
    originalUrl: String,
    customAlias: String?,
    expiresAt: LocalDateTime
  ): Mono<String> {
    val content = emailTemplateHandler.renderUrlCreatedEmail(
      userName = recipientName,
      shortUrl = shortUrl,
      originalUrl = originalUrl,
      customAlias = customAlias,
      expiresAt = LocalDateTime.now().plusDays(90)
    )

    return send(
      recipientEmail = recipientEmail,
      recipientName = recipientName,
      subject = "[Quickurl] 단축 URL이 생성되었습니다",
      htmlContent = content
    )
  }


  private fun sendEmail(emailRequest: BrevoEmailRequest): Mono<String> {
    return brevoWebClient.post()
      .uri("/v3/smtp/email")
      .bodyValue(emailRequest)
      .retrieve()
      .bodyToMono<String>()
      .doOnSuccess { log.info("Email sent successfully: $it") }
      .doOnError { log.error("Failed to send email", it) }
  }
}