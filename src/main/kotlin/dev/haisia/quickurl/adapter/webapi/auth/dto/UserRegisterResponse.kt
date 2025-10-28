package dev.haisia.quickurl.adapter.webapi.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class UserRegisterResponse(
  @JsonProperty("id")
  val id: String,
  @JsonProperty("email")
  val email: String
)