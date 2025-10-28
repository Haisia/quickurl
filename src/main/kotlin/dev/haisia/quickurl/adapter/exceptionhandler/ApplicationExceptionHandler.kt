package dev.haisia.quickurl.adapter.exceptionhandler

import dev.haisia.quickurl.adapter.webapi.ApiErrorData
import dev.haisia.quickurl.adapter.webapi.ApiResponse
import dev.haisia.quickurl.application.shared.ApplicationException
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApplicationExceptionHandler {
  companion object {
    private val log = LoggerFactory.getLogger(ApplicationExceptionHandler::class.java)
  }

  @ExceptionHandler(ApplicationException::class)
  fun handleApplicationException(e: ApplicationException): ResponseEntity<ApiResponse<ApiErrorData>> {
    log.warn("Application exception occurred: {}", e.message)

    return ResponseEntity
      .status(e.httpStatus)
      .body(ApiResponse.of(ApiErrorData.of(e.message ?: "Unknown application error")))
  }
}