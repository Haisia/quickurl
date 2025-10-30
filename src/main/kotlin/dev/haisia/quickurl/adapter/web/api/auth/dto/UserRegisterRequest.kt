package dev.haisia.quickurl.adapter.web.api.auth.dto

import dev.haisia.quickurl.domain.Email
import dev.haisia.quickurl.domain.Password
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "회원가입 요청")
data class UserRegisterRequest(
  @field:Schema(
    description = "사용자 이메일",
    example = "user@example.com",
    required = true
  )
  val email: Email,
  
  @field:Schema(
    description = "사용자 비밀번호",
    example = "password123!",
    required = true
  )
  val password: Password
)
