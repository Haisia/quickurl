package dev.haisia.quickurl.adapter.web.api.url.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ClickStatsResponse(
  @JsonProperty("short_key")
  val shortKey: String,

  @JsonProperty("total_clicks")
  val totalClicks: Long
)
