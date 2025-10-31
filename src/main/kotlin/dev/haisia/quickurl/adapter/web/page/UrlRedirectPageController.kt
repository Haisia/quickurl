package dev.haisia.quickurl.adapter.web.page

import dev.haisia.quickurl.application.url.dto.UrlClickDto
import dev.haisia.quickurl.application.url.`in`.UrlClicker
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.RedirectView

@Controller
class UrlRedirectPageController(
  private val urlClicker: UrlClicker,
) {

  @GetMapping("/{shortKey}")
  fun clickUrl(
    @PathVariable shortKey: String,
    request: HttpServletRequest
  ): ModelAndView {

    val originalUrl = urlClicker.click(
      UrlClickDto(
        shortKey = shortKey,
        ipAddress = getClientIp(request),
        userAgent = request.getHeader("User-Agent"),
        referer = request.getHeader("Referer")
      )
    )

    val redirectView = RedirectView(originalUrl.value)
    redirectView.setStatusCode(HttpStatus.FOUND)
    
    return ModelAndView(redirectView)
  }

  private fun getClientIp(request: HttpServletRequest): String {
    // Cloudflare의 실제 클라이언트 IP 헤더 우선 (Nginx에서 CF-Connecting-IP로 전달)
    request.getHeader("CF-Connecting-IP")?.takeIf { it.isNotBlank() }?.let { return it }
    
    // X-Real-IP 헤더 확인 (Nginx에서 설정)
    request.getHeader("X-Real-IP")?.takeIf { it.isNotBlank() }?.let { return it }
    
    // X-Forwarded-For 헤더 확인 (첫 번째 IP가 실제 클라이언트 IP)
    request.getHeader("X-Forwarded-For")?.takeIf { it.isNotBlank() }?.let {
      return it.split(",").first().trim()
    }
    
    // 마지막으로 remoteAddr 사용 (fallback)
    return request.remoteAddr
  }
}
