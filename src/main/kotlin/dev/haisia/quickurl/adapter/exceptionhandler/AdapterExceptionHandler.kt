package dev.haisia.quickurl.adapter.exceptionhandler

import dev.haisia.quickurl.adapter.AdapterException
import dev.haisia.quickurl.adapter.security.TokenExpiredException
import dev.haisia.quickurl.adapter.web.api.ApiErrorData
import dev.haisia.quickurl.adapter.web.api.ApiResponse
import dev.haisia.quickurl.adapter.web.api.ApiTokenExpiredData
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["dev.haisia.quickurl.adapter.web.api"])
class AdapterExceptionHandler {
  companion object {
    private val log = LoggerFactory.getLogger(AdapterExceptionHandler::class.java)
  }

  @ExceptionHandler(AdapterException::class)
  fun handleAdapterException(e: AdapterException): ResponseEntity<ApiResponse<ApiErrorData>> {
    log.warn("Adapter exception occurred: {}", e.message)

    return ResponseEntity
      .status(e.httpStatus)
      .body(ApiResponse.of(ApiErrorData.of(e.message ?: "Unknown application error")))
  }

  @ExceptionHandler(TokenExpiredException::class)
  fun handleTokenExpiredException(e: TokenExpiredException): ResponseEntity<ApiResponse<ApiTokenExpiredData>> {
    log.warn("Adapter exception occurred: {}", e.message)

    return ResponseEntity
      .status(e.httpStatus)
      .body(ApiResponse.of(ApiTokenExpiredData.accessToken()))
  }

}