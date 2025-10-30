package dev.haisia.quickurl.adapter.web.api.url.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "QR 코드 응답")
data class GetUrlQrCodeResponse(
  @field:Schema(
    description = "Base64 인코딩된 QR 코드 이미지",
    example = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg=="
  )
  @JsonProperty("qr_code_image")
  val qrCodeImage: String  // Base64 인코딩된 이미지 문자열
)
