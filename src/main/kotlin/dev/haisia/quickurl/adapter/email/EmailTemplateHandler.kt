package dev.haisia.quickurl.adapter.email

import dev.haisia.quickurl.adapter.config.AppProperties
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Service
class EmailTemplateHandler(
  private val templateEngine: TemplateEngine,
  private val appProperties: AppProperties
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
    shortKey: String,
    originalUrl: String,
    customAlias: String? = null,
    expiresAt: java.time.LocalDateTime? = null
  ): String {
    val context = Context().apply {
      setVariable("userName", userName)
      setVariable("shortKey", shortKey)
      setVariable("baseUrl", appProperties.baseUrl)
      setVariable("originalUrl", originalUrl)
      setVariable("customAlias", customAlias)
      setVariable("expiresAt", expiresAt)
    }
    return templateEngine.process("email/url-created", context)
  }
}
