package dev.haisia.quickurl.application.url.dto

import java.time.LocalDateTime

data class UrlWithClickCountDto(
  val id: Long,
  val shortKey: String,
  val originalUrl: String,
  val clickCount: Long,
  val createdBy: String,
  val lastUsedAt: LocalDateTime,
  val createdAt: LocalDateTime,
)