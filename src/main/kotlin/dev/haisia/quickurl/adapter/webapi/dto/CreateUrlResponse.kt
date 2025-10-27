package dev.haisia.quickurl.adapter.webapi.dto

import com.fasterxml.jackson.annotation.JsonProperty

class CreateUrlResponse(
  @JsonProperty("short_key")
  shortKey: String
)