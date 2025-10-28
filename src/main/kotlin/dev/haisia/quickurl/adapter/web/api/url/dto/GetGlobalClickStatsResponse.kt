package dev.haisia.quickurl.adapter.web.api.url.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GetGlobalClickStatsResponse(
  @JsonProperty("today_click_count")
  val todayClickCount: Long,
  @JsonProperty("total_click_count")
  val totalClickCount: Long,
)
