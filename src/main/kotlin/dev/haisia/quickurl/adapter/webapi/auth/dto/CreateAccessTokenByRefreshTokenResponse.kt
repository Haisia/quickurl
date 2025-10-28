package dev.haisia.quickurl.adapter.webapi.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateAccessTokenByRefreshTokenResponse(
  @JsonProperty("access_token")
  val accessToken: String,
)