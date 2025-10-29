package dev.haisia.quickurl.application.url.dto

import java.time.LocalDateTime

data class UrlClickDto(
  val shortKey: String,
  val ipAddress: String?,
  val userAgent: String?,
  val referer: String?,
  val clickedAt: LocalDateTime = LocalDateTime.now()
)