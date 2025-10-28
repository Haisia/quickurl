package dev.haisia.quickurl.adapter.web.api.auth.dto

data class UserInfoResponse(
  val email: String,
  val isLoggedIn: Boolean = true
)
