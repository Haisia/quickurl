package dev.haisia.quickurl.adapter.webapi.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class UserLoginResponse(
  @JsonProperty("access_token")
  val accessToken: String,
  @JsonProperty("refresh_token")
  val refreshToken: String
)