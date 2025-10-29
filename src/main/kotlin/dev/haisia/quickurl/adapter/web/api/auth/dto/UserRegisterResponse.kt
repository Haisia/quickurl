package dev.haisia.quickurl.adapter.web.api.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class UserRegisterResponse(
  @JsonProperty("id")
  val id: String,
  @JsonProperty("email")
  val email: String
)