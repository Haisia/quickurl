package dev.haisia.quickurl.adapter.exceptionhandler

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

/**
 * 페이지 컨트롤러 전역 예외 처리
 *
 * basePackages로 page 패키지만 지정하여,
 * 같은 예외라도 발생 위치에 따라 다르게 처리:
 * - page 패키지: HTML 에러 페이지 반환
 * - api 패키지: @RestControllerAdvice가 JSON 반환
 */
@ControllerAdvice(basePackages = ["dev.haisia.quickurl.adapter.web.page"])
class GlobalPageExceptionHandler {
  
  private val logger = LoggerFactory.getLogger(GlobalPageExceptionHandler::class.java)
  
  /**
   * 페이지 컨트롤러에서 발생한 모든 예외를 에러 페이지로 처리
   *
   * 예: CustomException이 페이지 컨트롤러에서 발생 → 이 핸들러가 처리 → HTML 반환
   */
  @ExceptionHandler(Exception::class)
  fun handleException(
    exception: Exception,
    request: HttpServletRequest,
    model: Model
  ): String {
    logger.error("페이지 처리 중 예외 발생: ${exception.message}", exception)
    
    model.addAttribute("message", exception.message ?: "알 수 없는 오류가 발생했습니다.")
    model.addAttribute("status", 500)
    model.addAttribute("path", request.requestURI)
    
    return "error"
  }
}