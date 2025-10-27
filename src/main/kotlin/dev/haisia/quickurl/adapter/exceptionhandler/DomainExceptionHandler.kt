package dev.haisia.quickurl.adapter.exceptionhandler

import dev.haisia.quickurl.adapter.webapi.dto.ApiErrorData
import dev.haisia.quickurl.adapter.webapi.dto.ApiResponse
import dev.haisia.quickurl.domain.DomainException
import dev.haisia.quickurl.domain.ShortKeyGenerationException
import dev.haisia.quickurl.domain.ShortKeyNotGeneratedException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class DomainExceptionHandler {
  companion object {
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
  }

  @ExceptionHandler(DomainException::class)
  fun handleDomainException(e: DomainException): ResponseEntity<ApiResponse<ApiErrorData>> {
    log.error("Domain exception occurred: {}", e.message)

    return ResponseEntity
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(ApiResponse.of(ApiErrorData.of(e.message ?: "Unknown domain error")))
  }

  @ExceptionHandler(ShortKeyGenerationException::class)
  fun handleShortKeyGenerationException(e: DomainException): ResponseEntity<ApiResponse<ApiErrorData>> {
    log.warn("Domain exception occurred: {}", e.message)

    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(ApiResponse.of(ApiErrorData.of(e.message ?: "Unknown domain error")))
  }

  @ExceptionHandler(ShortKeyNotGeneratedException::class)
  fun handleShortKeyNotGeneratedException(e: DomainException): ResponseEntity<ApiResponse<ApiErrorData>> {
    log.warn("Domain exception occurred: {}", e.message)

    return ResponseEntity
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(ApiResponse.of(ApiErrorData.of(e.message ?: "Unknown domain error")))
  }
}