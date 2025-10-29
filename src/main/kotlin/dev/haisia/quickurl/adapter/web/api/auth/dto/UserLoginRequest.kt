package dev.haisia.quickurl.adapter.web.api.auth.dto

import dev.haisia.quickurl.domain.Email
import dev.haisia.quickurl.domain.Password

data class UserLoginRequest(
  val email: Email,
  val password: Password
)
