package dev.haisia.quickurl.adapter.webapi.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class ApiResponse<T>(
  @JsonProperty("data")
  val data: T
) {
  companion object {
    fun <T> of(data: T): ApiResponse<T> {
      return ApiResponse(data)
    }

    // === Success Responses ===

    fun <T> ok(data: T): ResponseEntity<ApiResponse<T>> {
      return ResponseEntity.ok(of(data))
    }

    fun <T> created(data: T): ResponseEntity<ApiResponse<T>> {
      return ResponseEntity.status(HttpStatus.CREATED)
        .body(of(data))
    }

    fun noContent(): ResponseEntity<Void> {
      return ResponseEntity.noContent().build()
    }

    // === Error Responses ===

    fun badRequest(message: String): ResponseEntity<ApiResponse<ApiErrorData>> {
      return ResponseEntity.badRequest()
        .body(of(ApiErrorData.of(message)))
    }

    fun notFound(message: String): ResponseEntity<ApiResponse<ApiErrorData>> {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(of(ApiErrorData.of(message)))
    }

    fun conflict(message: String): ResponseEntity<ApiResponse<ApiErrorData>> {
      return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(of(ApiErrorData.of(message)))
    }

    fun tooManyRequests(message: String): ResponseEntity<ApiResponse<ApiErrorData>> {
      return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
        .body(of(ApiErrorData.of(message)))
    }

    fun forbidden(message: String): ResponseEntity<ApiResponse<ApiErrorData>> {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(of(ApiErrorData.of(message)))
    }

    fun internalServerError(message: String): ResponseEntity<ApiResponse<ApiErrorData>> {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(of(ApiErrorData.of(message)))
    }

    // === Generic ===

    fun <T> of(status: HttpStatus, data: T): ResponseEntity<ApiResponse<T>> {
      return ResponseEntity.status(status)
        .body(of(data))
    }

    fun errorWithStatus(status: HttpStatus, message: String): ResponseEntity<ApiResponse<ApiErrorData>> {
      return ResponseEntity.status(status)
        .body(of(ApiErrorData.of(message)))
    }
  }
}