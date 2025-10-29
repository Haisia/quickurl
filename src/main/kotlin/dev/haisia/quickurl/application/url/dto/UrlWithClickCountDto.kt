package dev.haisia.quickurl.application.url.dto

import dev.haisia.quickurl.domain.url.OriginalUrl
import java.time.LocalDateTime

data class UrlWithClickCountDto(
  val id: Long,
  val shortKey: String,
  val originalUrl: OriginalUrl,
  val clickCount: Long,
  val createdBy: String,
  val lastUsedAt: LocalDateTime,
  val createdAt: LocalDateTime,
)