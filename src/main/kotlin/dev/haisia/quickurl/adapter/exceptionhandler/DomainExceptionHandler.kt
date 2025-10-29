package dev.haisia.quickurl.adapter.exceptionhandler

import dev.haisia.quickurl.adapter.web.api.ApiErrorData
import dev.haisia.quickurl.adapter.web.api.ApiResponse
import dev.haisia.quickurl.domain.DomainException
import dev.haisia.quickurl.domain.url.InvalidOriginalUrlException
import dev.haisia.quickurl.domain.url.ShortKeyGenerationException
import dev.haisia.quickurl.domain.url.ShortKeyNotGeneratedException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class DomainExceptionHandler {
  companion object {
    private val log = LoggerFactory.getLogger(DomainExceptionHandler::class.java)
  }

  @ExceptionHandler(DomainException::class)
  fun handleDomainException(e: DomainException): ResponseEntity<ApiResponse<ApiErrorData>> {
    log.error("Domain exception occurred: {}", e.message)

    return ResponseEntity
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(ApiResponse.of(ApiErrorData.of(e.message ?: "Unknown domain error")))
  }

  @ExceptionHandler(InvalidOriginalUrlException::class)
  fun handleInvalidOriginalUrlException(e: InvalidOriginalUrlException): ResponseEntity<ApiResponse<ApiErrorData>> {
    log.warn("Invalid URL provided: {}", e.message)

    // 사용자 친화적인 메시지로 변환
    val userFriendlyMessage = convertToUserFriendlyMessage(e.message ?: "올바르지 않은 URL 형식입니다.")

    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(ApiResponse.of(ApiErrorData.of(userFriendlyMessage)))
  }

  private fun convertToUserFriendlyMessage(originalMessage: String): String {
    return when {
      originalMessage.contains("must have a protocol") || 
      originalMessage.contains("Invalid URL format") ->
        "URL은 http:// 또는 https://로 시작해야 합니다. (예: https://example.com)"
      
      originalMessage.contains("must have a valid host") ->
        "올바른 도메인 주소를 입력해주세요. (예: https://example.com)"
      
      originalMessage.contains("Only http and https protocols are allowed") ->
        "http:// 또는 https://로 시작하는 URL만 사용할 수 있습니다."
      
      originalMessage.contains("cannot be empty") ->
        "URL을 입력해주세요."
      
      originalMessage.contains("too long") ->
        "URL이 너무 깁니다. 더 짧은 URL을 입력해주세요."
      
      originalMessage.contains("potentially malicious content") ||
      originalMessage.contains("not allowed for security reasons") ->
        "보안상 허용되지 않는 URL입니다. 일반적인 웹 주소를 입력해주세요."
      
      else -> "올바르지 않은 URL 형식입니다. http:// 또는 https://로 시작하는 URL을 입력해주세요."
    }
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