package dev.haisia.quickurl.adapter.web

import dev.haisia.quickurl.application.shared.out.EmailSender
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RequestMapping("/test")
@RestController
class TestController(
  private val mailSender: EmailSender,
) {
  @GetMapping("/mail/welcome")
  fun sendWelcomeMail() {
    mailSender.sendWelcome(
      recipientEmail = "cwnsgur13579@gmail.com", 
      recipientName = "최준혁"
    ).subscribe()
  }

  @GetMapping("/mail/url-created")
  fun sendUrlCreatedMail() {
    mailSender.sendUrlCreated(
      recipientEmail = "cwnsgur13579@gmail.com",
      recipientName = "최준혁",
      shortUrl = "https://quickurl.haisia.dev/abc123",
      originalUrl = "https://www.example.com/very/long/url/path/that/needs/to/be/shortened",
      customAlias = "my-custom-link",
      expiresAt = LocalDateTime.now().plusDays(30)
    ).subscribe()
  }
}
