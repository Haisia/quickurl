package dev.haisia.quickurl.adapter.web.api.url.dto

import com.fasterxml.jackson.annotation.JsonProperty
import dev.haisia.quickurl.application.url.dto.UrlWithClickCountDto
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "내 URL 목록 응답")
data class GetMyUrlsResponse(
  @field:Schema(
    description = "단축 URL 키",
    example = "abc123"
  )
  @JsonProperty("short_key")
  val shortKey: String,

  @field:Schema(
    description = "원본 URL",
    example = "https://www.example.com/very/long/url/path"
  )
  @JsonProperty("original_url")
  val originalUrl: String,

  @field:Schema(
    description = "클릭 수",
    example = "42"
  )
  @JsonProperty("click_count")
  val clickCount: Long,

  @field:Schema(
    description = "생성 일시",
    example = "2025-01-15T10:30:00"
  )
  @JsonProperty("created_at")
  val createdAt: String,

  @field:Schema(
    description = "마지막 사용 일시",
    example = "2025-01-20T14:20:00"
  )
  @JsonProperty("last_used_at")
  val lastUsedAt: String,

  @field:Schema(
    description = "만료 일시 (null이면 만료되지 않음)",
    example = "2025-02-15T10:30:00"
  )
  @JsonProperty("expires_at")
  val expiresAt: String? = null
) {
  companion object {
    fun from(dto: UrlWithClickCountDto): GetMyUrlsResponse {
      return GetMyUrlsResponse(
        shortKey = dto.shortKey,
        originalUrl = dto.originalUrl.value,
        clickCount = dto.clickCount,
        createdAt = dto.createdAt.toString(),
        lastUsedAt = dto.lastUsedAt.toString(),
        expiresAt = dto.expiresAt?.toString()
      )
    }
  }
}
