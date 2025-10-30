package dev.haisia.quickurl.adapter.web.api.url.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "URL 클릭 통계 응답")
data class ClickStatsResponse(
  @field:Schema(
    description = "단축 URL 키",
    example = "abc123"
  )
  @JsonProperty("short_key")
  val shortKey: String,

  @field:Schema(
    description = "총 클릭 수",
    example = "150"
  )
  @JsonProperty("total_clicks")
  val totalClicks: Long
)
