package dev.haisia.quickurl.adapter.web.api.url.dto

import dev.haisia.quickurl.domain.Duration
import dev.haisia.quickurl.domain.url.OriginalUrl
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "URL 단축 요청")
data class CreateUrlRequest(
  @field:Schema(
    description = "원본 URL",
    example = "https://www.example.com/very/long/url/path",
    required = true
  )
  val originalUrl: OriginalUrl,
  
  @field:Schema(
    description = "만료 기간 (NONE, ONE_DAY, ONE_WEEK, ONE_MONTH, ONE_YEAR)",
    example = "ONE_WEEK",
    defaultValue = "NONE"
  )
  val expirationDuration: Duration = Duration.NONE,
)
