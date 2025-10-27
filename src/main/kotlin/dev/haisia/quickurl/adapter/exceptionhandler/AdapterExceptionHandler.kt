package dev.haisia.quickurl.adapter.exceptionhandler

import dev.haisia.quickurl.adapter.AdapterException
import dev.haisia.quickurl.adapter.webapi.dto.ApiErrorData
import dev.haisia.quickurl.adapter.webapi.dto.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
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

}