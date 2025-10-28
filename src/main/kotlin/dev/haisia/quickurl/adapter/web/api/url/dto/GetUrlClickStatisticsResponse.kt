package dev.haisia.quickurl.adapter.web.api.url.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GetUrlClickStatisticsResponse(
  @JsonProperty("daily_click_count")
  val dailyClickCount: Long,
  @JsonProperty("cumulative_click_count")
  val cumulativeClickCount: Long,
)
