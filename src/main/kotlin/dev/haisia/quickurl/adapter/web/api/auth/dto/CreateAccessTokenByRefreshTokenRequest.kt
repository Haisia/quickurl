package dev.haisia.quickurl.adapter.web.api.auth.dto

data class CreateAccessTokenByRefreshTokenRequest(
  val refreshToken: String
)