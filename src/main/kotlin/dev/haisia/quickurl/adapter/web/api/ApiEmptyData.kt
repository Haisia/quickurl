package dev.haisia.quickurl.adapter.web.api

import com.fasterxml.jackson.annotation.JsonProperty

data class ApiEmptyData(
  @JsonProperty("message")
  val message: String = "ok"
) {
  companion object {
    fun of(message: String): ApiEmptyData {
      return ApiEmptyData(message)
    }
  }
}