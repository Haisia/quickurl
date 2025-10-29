package dev.haisia.quickurl.adapter.email

import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Service
class EmailTemplateHandler(
  private val templateEngine: TemplateEngine
) {
  
  fun renderWelcomeEmail(userName: String, userEmail: String): String {
    val context = Context().apply {
      setVariable("userName", userName)
      setVariable("userEmail", userEmail)
    }
    return templateEngine.process("email/welcome", context)
  }
  
  fun renderUrlCreatedEmail(
    userName: String,
    shortUrl: String,
    originalUrl: String,
    customAlias: String? = null,
    expiresAt: java.time.LocalDateTime? = null
  ): String {
    val context = Context().apply {
      setVariable("userName", userName)
      setVariable("shortUrl", shortUrl)
      setVariable("originalUrl", originalUrl)
      setVariable("customAlias", customAlias)
      setVariable("expiresAt", expiresAt)
    }
    return templateEngine.process("email/url-created", context)
  }
}
