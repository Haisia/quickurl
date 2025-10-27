package dev.haisia.quickurl.adapter.webapi.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ApiErrorData(
  @JsonProperty("message")
  val message: String
) {
  companion object {
    fun of(message: String): ApiErrorData {
      return ApiErrorData(message)
    }
  }
}