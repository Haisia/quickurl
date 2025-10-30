package dev.haisia.quickurl.adapter.web.api.url.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GetUrlQrCodeResponse(
  @JsonProperty("qr_code_image")
  val qrCodeImage: String  // Base64 인코딩된 이미지 문자열
)