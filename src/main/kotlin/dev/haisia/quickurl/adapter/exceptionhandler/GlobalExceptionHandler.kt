package dev.haisia.quickurl.adapter.exceptionhandler

import dev.haisia.quickurl.adapter.web.api.ApiErrorData
import dev.haisia.quickurl.adapter.web.api.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.resource.NoResourceFoundException

/**
 * 전역 예외 처리 핸들러 (API용)
 * 
 * API 컨트롤러에서 발생한 예외를 JSON 형태로 반환합니다.
 * basePackages 설정으로 api 패키지만 처리하며,
 * page 패키지의 예외는 GlobalPageExceptionHandler가 처리합니다.
 */
@RestControllerAdvice(basePackages = ["dev.haisia.quickurl.adapter.web.api"])
class GlobalExceptionHandler {
  companion object {
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
  }

  /**
   * 404 Not Found - 존재하지 않는 리소스 요청
   * Spring Boot 3.x 이상에서는 NoResourceFoundException 사용
   */
  @ExceptionHandler(NoResourceFoundException::class)
  fun handleNoResourceFoundException(
    e: NoResourceFoundException,
    request: HttpServletRequest
  ): ResponseEntity<ApiResponse<ApiErrorData>> {
    log.info("Resource not found: {} {}", request.method, request.requestURI)

    return ResponseEntity
      .status(HttpStatus.NOT_FOUND)
      .body(ApiResponse.of(ApiErrorData.of("요청하신 리소스를 찾을 수 없습니다.")))
  }

  /**
   * 404 Not Found - 존재하지 않는 핸들러
   * Spring Boot 2.x 하위 호환성을 위한 처리
   */
  @ExceptionHandler(NoHandlerFoundException::class)
  fun handleNoHandlerFoundException(
    e: NoHandlerFoundException,
    request: HttpServletRequest
  ): ResponseEntity<ApiResponse<ApiErrorData>> {
    log.info("Handler not found: {} {}", request.method, request.requestURI)

    return ResponseEntity
      .status(HttpStatus.NOT_FOUND)
      .body(ApiResponse.of(ApiErrorData.of("요청하신 리소스를 찾을 수 없습니다.")))
  }

  /**
   * 405 Method Not Allowed - 지원하지 않는 HTTP 메소드
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
  fun handleHttpRequestMethodNotSupportedException(
    e: HttpRequestMethodNotSupportedException,
    request: HttpServletRequest
  ): ResponseEntity<ApiResponse<ApiErrorData>> {
    log.info(
      "Method not supported: {} {} (Supported: {})",
      request.method,
      request.requestURI,
      e.supportedHttpMethods?.joinToString(", ")
    )

    val supportedMethods = e.supportedHttpMethods?.joinToString(", ") ?: "없음"
    return ResponseEntity
      .status(HttpStatus.METHOD_NOT_ALLOWED)
      .body(ApiResponse.of(ApiErrorData.of("지원하지 않는 HTTP 메소드입니다. 지원하는 메소드: $supportedMethods")))
  }

  /**
   * 415 Unsupported Media Type - 지원하지 않는 Content-Type
   */
  @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
  fun handleHttpMediaTypeNotSupportedException(
    e: HttpMediaTypeNotSupportedException,
    request: HttpServletRequest
  ): ResponseEntity<ApiResponse<ApiErrorData>> {
    log.info(
      "Unsupported media type: {} {} (Content-Type: {})",
      request.method,
      request.requestURI,
      e.contentType
    )

    val supportedTypes = e.supportedMediaTypes.joinToString(", ")
    return ResponseEntity
      .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
      .body(ApiResponse.of(ApiErrorData.of("지원하지 않는 Content-Type입니다. 지원하는 타입: $supportedTypes")))
  }

  /**
   * 400 Bad Request - 요청 파라미터 누락
   */
  @ExceptionHandler(MissingServletRequestParameterException::class)
  fun handleMissingServletRequestParameterException(
    e: MissingServletRequestParameterException,
    request: HttpServletRequest
  ): ResponseEntity<ApiResponse<ApiErrorData>> {
    log.info("Missing required parameter: {} (type: {}) for {} {}", 
      e.parameterName, e.parameterType, request.method, request.requestURI)

    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(ApiResponse.of(ApiErrorData.of("필수 파라미터가 누락되었습니다: ${e.parameterName}")))
  }

  /**
   * 400 Bad Request - 유효하지 않은 요청 본문 (@Valid 검증 실패)
   */
  @ExceptionHandler(MethodArgumentNotValidException::class)
  fun handleMethodArgumentNotValidException(
    e: MethodArgumentNotValidException,
    request: HttpServletRequest
  ): ResponseEntity<ApiResponse<ApiErrorData>> {
    val errorMessages = e.bindingResult.fieldErrors
      .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
    
    log.info("Validation failed for {} {}: {}", request.method, request.requestURI, errorMessages)

    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(ApiResponse.of(ApiErrorData.of("입력값 검증에 실패했습니다: $errorMessages")))
  }

  /**
   * 400 Bad Request - 파라미터 타입 불일치
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException::class)
  fun handleMethodArgumentTypeMismatchException(
    e: MethodArgumentTypeMismatchException,
    request: HttpServletRequest
  ): ResponseEntity<ApiResponse<ApiErrorData>> {
    log.info(
      "Type mismatch for parameter: {} (expected: {}, actual: {}) for {} {}",
      e.name,
      e.requiredType?.simpleName,
      e.value,
      request.method,
      request.requestURI
    )

    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(ApiResponse.of(ApiErrorData.of(
        "잘못된 파라미터 타입입니다: ${e.name} (기대값: ${e.requiredType?.simpleName})"
      )))
  }

  /**
   * 400 Bad Request - JSON 파싱 오류 또는 읽을 수 없는 요청 본문
   */
  @ExceptionHandler(HttpMessageNotReadableException::class)
  fun handleHttpMessageNotReadableException(
    e: HttpMessageNotReadableException,
    request: HttpServletRequest
  ): ResponseEntity<ApiResponse<ApiErrorData>> {
    log.info("Message not readable for {} {}: {}", request.method, request.requestURI, e.message)

    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(ApiResponse.of(ApiErrorData.of("잘못된 요청 형식입니다. JSON 형식을 확인해주세요.")))
  }

  /**
   * 400 Bad Request - 일반적인 IllegalArgumentException
   */
  @ExceptionHandler(IllegalArgumentException::class)
  fun handleIllegalArgumentException(
    e: IllegalArgumentException,
    request: HttpServletRequest
  ): ResponseEntity<ApiResponse<ApiErrorData>> {
    log.info("Illegal argument for {} {}: {}", request.method, request.requestURI, e.message)

    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(ApiResponse.of(ApiErrorData.of(e.message ?: "잘못된 요청입니다.")))
  }

  /**
   * 500 Internal Server Error - 일반적인 IllegalStateException
   */
  @ExceptionHandler(IllegalStateException::class)
  fun handleIllegalStateException(
    e: IllegalStateException,
    request: HttpServletRequest
  ): ResponseEntity<ApiResponse<ApiErrorData>> {
    log.error("Illegal state for {} {}: {}", request.method, request.requestURI, e.message, e)

    return ResponseEntity
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(ApiResponse.of(ApiErrorData.of("서버 내부 오류가 발생했습니다.")))
  }

  /**
   * 500 Internal Server Error - 모든 예외의 최종 처리
   * 
   * 위의 핸들러들에서 처리되지 않은 모든 예외를 처리합니다.
   * 전체 스택 트레이스를 로그에 남기며, 클라이언트에는 일반적인 메시지만 전달합니다.
   */
  @ExceptionHandler(Exception::class)
  fun handleException(
    e: Exception,
    request: HttpServletRequest
  ): ResponseEntity<ApiResponse<ApiErrorData>> {
    log.error("Unhandled exception for {} {}", request.method, request.requestURI, e)

    return ResponseEntity
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(ApiResponse.of(ApiErrorData.of("서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")))
  }
}
