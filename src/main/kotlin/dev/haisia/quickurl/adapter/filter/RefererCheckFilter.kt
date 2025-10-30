package dev.haisia.quickurl.adapter.filter

import dev.haisia.quickurl.adapter.config.SecurityProperties
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.net.URI

/**
 * Referer 헤더를 검증하여 Thymeleaf 화면에서만 API 호출을 허용하는 필터
 *
 * 이 필터는 특정 API 경로에 대해 Referer 헤더가 허용된 도메인에서 온 것인지 검증합니다.
 * 외부 도구(Postman, curl 등)에서의 직접 API 호출을 차단합니다.
 */
@Component
class RefererCheckFilter(
  private val securityProperties: SecurityProperties
) : OncePerRequestFilter() {
  
  private val log = LoggerFactory.getLogger(javaClass)
  
  companion object {
    private const val REFERER_HEADER = "Referer"
    private const val ORIGIN_HEADER = "Origin"
  }
  
  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain
  ) {
    // 필터가 비활성화된 경우 검증 없이 통과
    if (!securityProperties.refererCheck.enabled) {
      filterChain.doFilter(request, response)
      return
    }
    
    val requestPath = request.requestURI
    
    // 검증 대상 경로인지 확인
    if (shouldCheckReferer(requestPath)) {
      if (!isValidReferer(request)) {
        log.warn(
          "Invalid referer detected - Path: {}, Referer: {}, Origin: {}, IP: {}",
          requestPath,
          request.getHeader(REFERER_HEADER),
          request.getHeader(ORIGIN_HEADER),
          request.remoteAddr
        )
        
        response.sendError(
          HttpServletResponse.SC_FORBIDDEN,
          "Direct API access is not allowed. Please use the web interface."
        )
        return
      }
    }
    
    filterChain.doFilter(request, response)
  }
  
  /**
   * 해당 경로가 Referer 검증 대상인지 확인
   */
  private fun shouldCheckReferer(path: String): Boolean {
    // 제외 경로 체크
    if (securityProperties.refererCheck.excludedPaths.any { path.startsWith(it) }) {
      return false
    }
    
    // 보호 대상 경로 체크
    return securityProperties.refererCheck.protectedPaths.any { path.startsWith(it) }
  }
  
  /**
   * Referer 또는 Origin 헤더가 유효한지 검증
   */
  private fun isValidReferer(request: HttpServletRequest): Boolean {
    val referer = request.getHeader(REFERER_HEADER)
    val origin = request.getHeader(ORIGIN_HEADER)
    
    // Referer와 Origin이 둘 다 없으면 차단
    if (referer.isNullOrBlank() && origin.isNullOrBlank()) {
      log.debug("No Referer or Origin header found")
      return false
    }
    
    // Referer 검증
    if (!referer.isNullOrBlank()) {
      if (isAllowedDomain(referer)) {
        return true
      }
    }
    
    // Origin 검증 (Referer가 없거나 유효하지 않은 경우)
    if (!origin.isNullOrBlank()) {
      if (isAllowedDomain(origin)) {
        return true
      }
    }
    
    return false
  }
  
  /**
   * URL이 허용된 도메인인지 확인
   */
  private fun isAllowedDomain(url: String): Boolean {
    return try {
      val uri = URI(url)
      val host = uri.host ?: return false
      val scheme = uri.scheme ?: return false
      val port = if (uri.port == -1) "" else ":${uri.port}"
      
      val fullUrl = "$scheme://$host$port"
      
      // 설정된 허용 도메인 목록과 비교
      val isAllowed = securityProperties.refererCheck.allowedDomains.any { allowedDomain ->
        // 정확히 일치하거나, 서브도메인을 허용하는 경우
        fullUrl.equals(allowedDomain, ignoreCase = true) ||
        (securityProperties.refererCheck.allowSubdomains &&
        fullUrl.endsWith(".$allowedDomain", ignoreCase = true))
      }
      
      log.debug("Domain check - URL: {}, Host: {}, Allowed: {}", url, fullUrl, isAllowed)
      isAllowed
    } catch (e: Exception) {
      log.warn("Failed to parse URL: {}", url, e)
      false
    }
  }
}
