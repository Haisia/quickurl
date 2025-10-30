package dev.haisia.quickurl.adapter.web.api.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "사용자 정보 응답")
data class UserInfoResponse(
  @field:Schema(
    description = "사용자 이메일",
    example = "user@example.com"
  )
  @JsonProperty("email")
  val email: String,
  
  @field:Schema(
    description = "로그인 여부",
    example = "true"
  )
  @JsonProperty("is_logged_in")
  val isLoggedIn: Boolean = true
)
