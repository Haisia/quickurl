package dev.haisia.quickurl.adapter.web.api.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class UserInfoResponse(
  @JsonProperty("email")
  val email: String,
  @JsonProperty("is_logged_in")
  val isLoggedIn: Boolean = true
)
