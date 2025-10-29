package dev.haisia.quickurl.adapter.web.page

import jakarta.servlet.RequestDispatcher
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

/**
 * 애플리케이션 전역 에러 처리 컨트롤러
 * Spring Boot의 기본 Whitelabel Error Page를 커스텀 에러 페이지로 대체
 */
@Controller
class CustomErrorController : ErrorController {

  /**
   * 에러 발생 시 호출되는 핸들러
   * 
   * @param request HTTP 요청 객체 (에러 정보 포함)
   * @param model 뷰에 전달할 데이터
   * @return 에러 페이지 템플릿 경로
   */
  @RequestMapping("/error")
  fun handleError(request: HttpServletRequest, model: Model): String {
    // 에러 상태 코드 추출
    val status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)
    val statusCode = status?.toString()?.toIntOrNull() ?: 500

    // 에러 메시지 추출
    val exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION) as? Throwable
    val errorMessage = when {
      exception != null -> exception.message ?: "알 수 없는 오류가 발생했습니다."
      statusCode == 401 -> "인증이 필요합니다. 로그인해주세요."
      statusCode == 403 -> "접근 권한이 없습니다."
      statusCode == 404 -> "요청하신 페이지를 찾을 수 없습니다."
      statusCode == 500 -> "서버 내부 오류가 발생했습니다."
      else -> "알 수 없는 오류가 발생했습니다."
    }

    // 요청 경로 추출
    val path = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)?.toString() 
      ?: request.requestURI

    // 모델에 에러 정보 추가
    model.addAttribute("status", statusCode)
    model.addAttribute("message", errorMessage)
    model.addAttribute("path", path)

    // HTTP 상태 코드 설정
    request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, statusCode)

    return "error"
  }
}
