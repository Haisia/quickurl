package dev.haisia.quickurl.adapter.web.api.url.dto

import com.fasterxml.jackson.annotation.JsonProperty
import dev.haisia.quickurl.application.url.dto.UrlWithClickCountDto

data class GetMyUrlsResponse(
  @JsonProperty("short_key")
  val shortKey: String,

  @JsonProperty("original_url")
  val originalUrl: String,

  @JsonProperty("click_count")
  val clickCount: Long,

  @JsonProperty("created_at")
  val createdAt: String,

  @JsonProperty("last_used_at")
  val lastUsedAt: String
) {
  companion object {
    fun from(dto: UrlWithClickCountDto): GetMyUrlsResponse {
      return GetMyUrlsResponse(
        shortKey = dto.shortKey,
        originalUrl = dto.originalUrl.value,
        clickCount = dto.clickCount,
        createdAt = dto.createdAt.toString(),
        lastUsedAt = dto.lastUsedAt.toString()
      )
    }
  }
}