package dev.haisia.quickurl.adapter.email

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class EmailConfig {
  @Value("\${brevo.api-key}")
  private lateinit var brevoApiKey: String

  @Value("\${brevo.api-base-url:https://api.brevo.com}")
  private lateinit var brevoBaseUrl: String

  @Bean
  fun brevoWebClient(): WebClient {
    return WebClient.builder()
      .baseUrl(brevoBaseUrl)
      .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
      .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
      .defaultHeader("Api-Key", brevoApiKey)
      .build()
  }
}