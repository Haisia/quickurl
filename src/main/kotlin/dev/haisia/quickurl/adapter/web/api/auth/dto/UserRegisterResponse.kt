package dev.haisia.quickurl.adapter.web.api.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "회원가입 응답")
data class UserRegisterResponse(
  @field:Schema(
    description = "생성된 사용자 ID",
    example = "550e8400-e29b-41d4-a716-446655440000"
  )
  @JsonProperty("id")
  val id: String,
  
  @field:Schema(
    description = "등록된 이메일",
    example = "user@example.com"
  )
  @JsonProperty("email")
  val email: String
)
