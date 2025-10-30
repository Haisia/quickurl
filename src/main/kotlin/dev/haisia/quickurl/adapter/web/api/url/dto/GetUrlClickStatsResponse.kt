package dev.haisia.quickurl.adapter.web.api.url.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "전체 클릭 통계 응답")
data class GetUrlClickStatsResponse(
  @field:Schema(
    description = "일일 클릭 수 (오늘)",
    example = "42"
  )
  @JsonProperty("daily_click_count")
  val dailyClickCount: Long,
  
  @field:Schema(
    description = "누적 클릭 수 (전체)",
    example = "1543"
  )
  @JsonProperty("cumulative_click_count")
  val cumulativeClickCount: Long,
)
