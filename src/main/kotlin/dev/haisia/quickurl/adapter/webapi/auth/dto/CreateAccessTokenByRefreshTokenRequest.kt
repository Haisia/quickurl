package dev.haisia.quickurl.adapter.webapi.auth.dto

data class CreateAccessTokenByRefreshTokenRequest(
  val refreshToken: String
)