package dev.haisia.quickurl.adapter.web.api.url.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateUrlResponse(
  @JsonProperty("short_key")
  val shortKey: String
)