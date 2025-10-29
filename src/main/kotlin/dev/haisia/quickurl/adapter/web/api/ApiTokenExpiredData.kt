package dev.haisia.quickurl.adapter.web.api

import com.fasterxml.jackson.annotation.JsonProperty

class ApiTokenExpiredData (
  @JsonProperty("type")
  val type: String,

  @JsonProperty("is_expired")
  val isExpired: Boolean = true
) {
  companion object {
    fun accessToken(): ApiTokenExpiredData {
      return ApiTokenExpiredData("access_token", true)
    }

    fun refreshToken(): ApiTokenExpiredData {
      return ApiTokenExpiredData("refresh_token", true)
    }
  }
}