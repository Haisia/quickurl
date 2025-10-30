package dev.haisia.quickurl.adapter.web.api.url.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "URL 단축 응답")
data class CreateUrlResponse(
  @field:Schema(
    description = "생성된 단축 URL 키",
    example = "abc123"
  )
  @JsonProperty("short_key")
  val shortKey: String
)
