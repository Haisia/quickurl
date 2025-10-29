package dev.haisia.quickurl.application.shared.out

import dev.haisia.quickurl.domain.url.OriginalUrl
import reactor.core.publisher.Mono
import java.time.LocalDateTime

interface EmailSender {
  fun send(
    recipientEmail: String,
    recipientName: String,
    subject: String,
    htmlContent: String
  ): Mono<String>

  fun sendWelcome(recipientEmail: String, recipientName: String): Mono<String>

  fun sendUrlCreated(
    recipientEmail: String,
    recipientName: String,
    shortUrl: String,
    originalUrl: OriginalUrl,
    customAlias: String? = null,
    expiresAt: LocalDateTime = LocalDateTime.now().plusDays(90)
  ): Mono<String>
}